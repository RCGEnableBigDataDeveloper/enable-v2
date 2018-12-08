package com.rcggs.datalake.core.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
//import javax.swing.JFrame;
//import javax.swing.JTree;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.rcggs.datalake.pool.DataSource;

public class LevelBuilder {

	final static ObjectWriter ow = new ObjectMapper().writer();

	private List<String[]> data = new ArrayList<String[]>();

	public static void main(final String[] args) throws Exception {
		LevelBuilder v = new LevelBuilder();
		
		String parentQry = "select * from dl_wf_layout_fldr order by seq";
		String childQry = "select * from dl_wf_layout where parent_fldr_id=";
		
		System.err.println(v.build(parentQry, childQry));
	}

	public String build(final String parent, final String children) {

		Connection conn = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			conn = DataSource.getConnection();			
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(parent);
			while (rs.next()) {
				data.add(new String[] { rs.getString("seq"),
						rs.getString("name"), rs.getString("parent_fldr_id"),
						"folder" });
				Statement stmt1 = conn.createStatement();
				rs1 = stmt1
						.executeQuery(children + "'"
								+ rs.getString("name") + "'");
				while (rs1.next()) {
					data.add(new String[] { UUID.randomUUID().toString(),
							rs1.getString("name"), rs.getString("seq"), "file" });
				}
			}
			Level root = new Level();
			root.setName("Saved Layouts");
			root.setId("0");
			createTreeNodesForElement(root, getElementTreeFromPlainList());
			return (ow.writeValueAsString(root));

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs!=null) rs.close();
				if (rs1!=null) rs1.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	Collection<Element> getElementTreeFromPlainList() {

		Map<String, Element> values = new HashMap<String, Element>();
		for (String[] s : data) {
			values.put(s[0], new Element(s[0], s[2], s[1], s[3]));
		}
		Collection<Element> result = new ArrayList<LevelBuilder.Element>();
		for (Element e : values.values()) {
			if (e.parent != null) {
				values.get(e.parent).getChildren().add(e);
			} else {
				result.add(e);
			}
		}
		return result;
	}

	void createTreeNodesForElement(final Level dmtn,
			final Collection<Element> elements) {

		for (Element child : elements) {
			Level created = new Level();
			created.setId(child.getId());
			created.setName(child.getName());
			created.setParent(child.getId());
			created.setType(child.getType());
			created.setIconCls(child.getType().equals("folder") ? "tree-folder"
					: "tree-file");
			if (dmtn.getChildren() == null) {
				List<Level> list = new ArrayList<Level>();
				list.add(created);
				dmtn.setChildren(list);
			} else {
				dmtn.getChildren().add(created);
			}

			createTreeNodesForElement(created, child.getChildren());
		}
	}

	public class Element {

		private final String parent;
		private final String name;
		private final String type;
		private final String id;
		private final Collection<Element> children = new ArrayList<LevelBuilder.Element>();

		public Element(final String id, final String parent, final String name,
				final String type) {
			super();
			this.parent = parent;
			this.name = name;
			this.type = type;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getParent() {
			return parent;
		}

		public String getType() {
			return type;
		}

		public Collection<Element> getChildren() {
			return children;
		}

		public String getId() {
			return id;
		}
	}
}