package com.rcggs.datalake.core.model;

public class Metric {

	private String id;
	private String srcId;
	private String jobId;
	private String targetId;
	private String name;
	private String type;
	private String description;
	private String message;
	private long time;
	private String dataSetName;
	private String fileTimeStamp;
	private int totalRows;
	private int totalColumns;
	private int totalFiles;
	private String routeId;
	private String nodeId;
	private String fileSize;
	private String status;
	private String messageType;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSrcId() {
		return srcId;
	}

	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public String getFileTimeStamp() {
		return fileTimeStamp;
	}

	public void setFileTimeStamp(String fileTimeStamp) {
		this.fileTimeStamp = fileTimeStamp;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public int getTotalColumns() {
		return totalColumns;
	}

	public void setTotalColumns(int totalColumns) {
		this.totalColumns = totalColumns;
	}

	public int getTotalFiles() {
		return totalFiles;
	}

	public void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
}