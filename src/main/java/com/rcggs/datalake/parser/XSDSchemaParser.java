package com.rcggs.datalake.parser;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.rcggs.datalake.core.model.SchemaDef;
import com.rcggs.enable.data.resource.ResourceReader;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.impl.AttributeUseImpl;
import com.sun.xml.xsom.parser.XSOMParser;

public class XSDSchemaParser extends ResourceReader<SchemaDef> implements SchemaParser {

	@Override
	public void parse(String filePath) throws Exception {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		XSDSchemaParser x = new XSDSchemaParser();
		Map<String, String> map = new HashMap<>();
		map.put("gsdn", "/tmp/Schemas/item/2.0/");
		x.read("CatalogueItemNotification.xsd", "", map);
	}

	@Override
	public List<SchemaDef> read(String path, String type, Map<String, String> pathMap) {

		final List<SchemaDef> schemas = new LinkedList<>();
		try {
			
			XSOMParser parser = new XSOMParser();
			//parser.parse(new File(pathMap.get("gsdn") + path));
			
			parser.parse(new File("/tmp/schemas/item/2.0/CatalogueItemNotification.xsd"));
			
			XSSchemaSet schemaSet = parser.getResult();
			// XSSchema xsSchema = schemaSet.getSchema(4);

			System.err.println(schemaSet);

			for (Iterator<XSSchema> iter = schemaSet.getSchemas().iterator(); iter.hasNext();) {

				// iter.next(); // --skipping
				XSSchema xsSchema = iter.next();
				String urn = xsSchema.getLocator().getSystemId();
				String namespace = xsSchema.getTargetNamespace();

				for (Entry<String, XSSimpleType> entry : xsSchema.getSimpleTypes().entrySet()) {
					XSSimpleType simpleType = entry.getValue();
				//	System.out.println("SIMPLE: " + entry.getKey() + " : " + entry.getValue().getBaseType().getName());

					SchemaDef def = new SchemaDef();
					def.setName(entry.getKey());
					def.setValue(entry.getValue().getBaseType().getName());
					def.setType("simple");
					def.setGroup(entry.getKey());
					def.setNamespace(namespace);
					def.setUrn(urn);

					List<String> restrictionsList = new LinkedList<>();
					if (simpleType.isRestriction()) {
						for (Iterator<? extends XSFacet> restrictions = simpleType.asRestriction().getDeclaredFacets()
								.iterator(); restrictions.hasNext();) {
							restrictionsList.add(restrictions.next().getValue().value);
						}

						def.setRestrictions(restrictionsList);
					}

					schemas.add(def);
				}

				for (Entry<String, XSComplexType> entry : xsSchema.getComplexTypes().entrySet()) {

					System.out.println("COMPLEX: " + entry.getKey() + " : " + entry.getValue().getBaseType().getName());

					SchemaDef def = new SchemaDef();
					def.setName(entry.getKey());
					def.setValue(entry.getValue().getBaseType().getName());
					def.setType("complex");
					def.setGroup(entry.getKey());
					def.setNamespace(namespace);
					def.setUrn(urn);
					schemas.add(def);

					Collection<?> attrs = entry.getValue().getAttributeUses();
					for (Iterator<?> it1 = attrs.iterator(); it1.hasNext();) {
						AttributeUseImpl attr = (AttributeUseImpl) it1.next();
						// System.out.println(
						// "ATTRIBUTE: " + entry.getKey() + " : "
						// +attr.getDecl().getName() + " : " +
						// attr.getDecl().getType().getBaseType().getName());

						SchemaDef adef = new SchemaDef();
						adef.setName(attr.getDecl().getName());
						adef.setValue(attr.getDecl().getType().getBaseType().getName());
						adef.setType("attribute");
						adef.setGroup(entry.getKey());
						adef.setNamespace(namespace);
						adef.setUrn(urn);
						schemas.add(adef);

					}
					XSParticle part = entry.getValue().getContentType().asParticle();
					if ((part != null) && (part.getTerm().isModelGroup())) {
						XSParticle[] parts = part.getTerm().asModelGroup().getChildren();
						for (XSParticle s : parts) {
							if (s.getTerm().isElementDecl()) {
								System.out.println("SEQUENCE: " + s.getTerm().asElementDecl().getName() + " : "
										+ s.getTerm().asElementDecl().getType().getName());

								SchemaDef adef = new SchemaDef();
								adef.setName(s.getTerm().asElementDecl().getName());
								adef.setValue(s.getTerm().asElementDecl().getType().getName());
								adef.setType("sequence");
								adef.setGroup(entry.getKey());
								adef.setNamespace(namespace);
								adef.setUrn(urn);
								schemas.add(adef);

							} else if (s.getTerm().isModelGroupDecl()) {
								XSModelGroupDecl group = s.getTerm().asModelGroupDecl();
								System.out.println("GROUP: " + group.getName());

								SchemaDef gdef = new SchemaDef();
								gdef.setName(group.getName());
								gdef.setValue("group");
								gdef.setType("group");
								gdef.setGroup(group.getName());
								gdef.setNamespace(namespace);
								gdef.setUrn(urn);
								schemas.add(gdef);

								for (XSParticle x : s.getTerm().asModelGroupDecl().getModelGroup().getChildren()) {
									// System.out.println("GROUP ELEMENT: " +
									// x.getTerm().asElementDecl().getName() + "
									// : "
									// +
									// x.getTerm().asElementDecl().getType().getName());

									SchemaDef adef = new SchemaDef();
									adef.setName(x.getTerm().asElementDecl().getName());
									adef.setValue(x.getTerm().asElementDecl().getType().getName());
									adef.setType("group element");
									adef.setType(group.getName());
									adef.setNamespace(namespace);
									adef.setUrn(urn);
									schemas.add(adef);
								}
							} else {
								 System.err.println(s.getTerm());
							}
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return schemas;
	}
}