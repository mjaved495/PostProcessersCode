package edu.cornell.scholars.keywordminer;

public class Mesh {

	private String meshURI;
	private String meshLabel;
	
	public Mesh(String meshURI, String meshLabel) {
		super();
		this.meshURI = meshURI;
		this.meshLabel = meshLabel;
	}
	public Mesh() {
	}
	
	public String getMeshURI() {
		return meshURI;
	}
	public void setMeshURI(String meshURI) {
		this.meshURI = meshURI;
	}
	public String getMeshLabel() {
		return meshLabel;
	}
	public void setMeshLabel(String meshLabel) {
		this.meshLabel = meshLabel;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((meshLabel == null) ? 0 : meshLabel.hashCode());
		result = prime * result + ((meshURI == null) ? 0 : meshURI.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mesh other = (Mesh) obj;
		if (meshLabel == null) {
			if (other.meshLabel != null)
				return false;
		} else if (!meshLabel.equals(other.meshLabel))
			return false;
		if (meshURI == null) {
			if (other.meshURI != null)
				return false;
		} else if (!meshURI.equals(other.meshURI))
			return false;
		return true;
	}
	
	
	
	
}
