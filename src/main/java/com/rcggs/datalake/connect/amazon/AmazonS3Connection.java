package com.rcggs.datalake.connect.amazon;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;

public class AmazonS3Connection extends AbstractDataLakeConnection implements DataLakeConnection {

	ConnectionConfig config;

	public AmazonS3Connection(ConnectionConfig config) {
		super(config);
		this.config = config;
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) throws Exception {
		AmazonS3 s3;
		Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);
		try {

			AWSCredentials awscreds = new BasicAWSCredentials(String.valueOf(config.getProperties().get("accessKey")),
					String.valueOf(config.getProperties().get("secretKey")));

			s3 = new AmazonS3Client(awscreds);
		} catch (Exception e) {
			throw new AmazonClientException("error connecting to aws", e);
		}

		s3.setEndpoint(String.valueOf(config.getProperties().get("endpoint")));

		try {

			Map<String, List<ItemDefinition>> folders = new HashMap<String, List<ItemDefinition>>();

			ArrayList<String> arrayList = new ArrayList<String>();
			ObjectListing objectListing = s3.listObjects(new ListObjectsRequest().withBucketName(config.getPath()));

			List<ItemDefinition> children = new ArrayList<ItemDefinition>();
			ItemDefinition idef = new ItemDefinition();
			idef.setName(config.getPath());

			map.put(idef, new ArrayList<ItemDefinition>());

			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {

				if (objectSummary.getKey().contains(":"))
					continue;

				Path path = Paths.get(objectSummary.getKey());

				// String folder = null;
				// if (path.getParent() == null) {
				// folder = path.getFileName().toString();
				// } else {
				// folder = path.getParent().toString();
				// }
				//
				// if (folders.get(folder) == null) {
				// folders.put(folder, new ArrayList<ItemDefinition>());
				// } else {

				ItemDefinition cd = new ItemDefinition();
				cd.setName(path.getFileName().toString());
				cd.setData(data);
				cd.setItemType("file");
				// folders.get(path.getParent().toString()).add(cd);
				children.add(cd);
				// }

				arrayList.add(objectSummary.getKey() + "  " + objectSummary.getSize());
			}

			idef.setChildren(children);

			map.put(idef, children);

			// for (String folderName : folders.keySet()) {
			//
			// ItemDefinition idef = new ItemDefinition();
			// idef.setName(folderName);
			// idef.setItemType("directory");
			// idef.setData(data);
			//
			// map.put(idef, folders.get(folderName));
			// System.out.println(map);
			// }

		} catch (AmazonServiceException ase) {
			ase.printStackTrace();
		}

		return map;
	}
}