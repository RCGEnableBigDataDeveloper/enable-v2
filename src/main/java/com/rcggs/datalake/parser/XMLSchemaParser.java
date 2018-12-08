package com.rcggs.datalake.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.rcggs.datalake.core.model.SchemaDef;
import com.rcggs.datalake.resource.ResourceReader;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.impl.AttributeUseImpl;
import com.sun.xml.xsom.parser.XSOMParser;

public class XMLSchemaParser extends ResourceReader<SchemaDef> implements SchemaParser {

	@Override
	public void parse(String filePath) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public List<SchemaDef> read(String path, String type, Map<String, String> pathMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		try {
			XSOMParser parser = new XSOMParser();
			parser.parse(new File("/tmp/source.xsd"));
			XSSchemaSet schemaSet = parser.getResult();
			XSSchema xsSchema = schemaSet.getSchema(1);

			System.out.println(xsSchema.getTargetNamespace());

			for (Entry<String, XSSimpleType> entry : xsSchema.getSimpleTypes().entrySet()) {
				XSSimpleType simpleType = entry.getValue();
				System.out.println("SIMPLE: " + entry.getKey() + " : " + entry.getValue().getBaseType().getName());
				if (simpleType.isRestriction()) {
					for (Iterator<? extends XSFacet> restrictions = simpleType.asRestriction().getDeclaredFacets()
							.iterator(); restrictions.hasNext();) {
						System.out.println("RESTRICTION: " + restrictions.next().getValue());
					}
				}

				System.out.println();
			}

			for (Entry<String, XSComplexType> entry : xsSchema.getComplexTypes().entrySet()) {
				System.out.println("COMPLEX: " + entry.getKey() + " : " + entry.getValue().getBaseType().getName());
				Collection<?> attrs = entry.getValue().getAttributeUses();
				for (Iterator<?> it1 = attrs.iterator(); it1.hasNext();) {
					AttributeUseImpl attr = (AttributeUseImpl) it1.next();
					System.out.println(
							"ATTRIBUTE: " + attr.getDecl().getName() + " : " + attr.getDecl().getType().getName());
				}
				XSParticle part = entry.getValue().getContentType().asParticle();
				if ((part != null) && (part.getTerm().isModelGroup())) {
					XSParticle[] parts = part.getTerm().asModelGroup().getChildren();
					for (XSParticle s : parts) {
						if (s.getTerm().isElementDecl()) {
							System.out.println("SEQUENCE: " + s.getTerm().asElementDecl().getName() + " : "
									+ s.getTerm().asElementDecl().getType().getName());
						} else if (s.getTerm().isModelGroupDecl()) {
							XSModelGroupDecl group = s.getTerm().asModelGroupDecl();
							System.out.println("GROUP: " + group.getName());
							for (XSParticle x : s.getTerm().asModelGroupDecl().getModelGroup().getChildren()) {
								System.out.println("GROUP ELEMENT: " + x.getTerm().asElementDecl().getName() + " : "
										+ x.getTerm().asElementDecl().getType().getName());
							}
						} else {
							System.err.println(s.getTerm());
						}
					}

				}
				System.out.println();
			}

		} catch (Exception exp) {
			exp.printStackTrace(System.out);
		}
	}
}