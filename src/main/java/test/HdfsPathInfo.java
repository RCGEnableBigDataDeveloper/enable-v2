package test;

import java.util.concurrent.atomic.AtomicLong;

class HdfsPathInfo {
	private AtomicLong size;
	private AtomicLong total;
	private long accessTime;
	private long blockSize;
	private long lastModifed;
	private String group;
	private String owner;
	private short replicationFactor;

	public AtomicLong getSize() {
		return size;
	}

	public void setSize(AtomicLong size) {
		this.size = size;
	}

	public AtomicLong getTotal() {
		return total;
	}

	public void setTotal(AtomicLong total) {
		this.total = total;
	}

	public long getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(long accessTime) {
		this.accessTime = accessTime;
	}

	public long getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}

	public long getLastModifed() {
		return lastModifed;
	}

	public void setLastModifed(long lastModifed) {
		this.lastModifed = lastModifed;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public short getReplicationFactor() {
		return replicationFactor;
	}

	public void setReplicationFactor(short replicationFactor) {
		this.replicationFactor = replicationFactor;
	}
}