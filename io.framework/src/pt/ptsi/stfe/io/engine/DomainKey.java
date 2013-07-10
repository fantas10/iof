/**
 * 
 */
package pt.ptsi.stfe.io.engine;

import java.io.Serializable;

/**
 * @author XPTS912
 *
 */
public final class DomainKey implements IOKey, Serializable {

	private final String key;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DomainKey(String key) {
		this.key = key;
	}

	/* (non-Javadoc)
	 * @see pt.ptsi.stfe.io.engine.IOKey#getKey()
	 */
	@Override
	public String getKey() {
		return this.key;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof DomainKey) {
			DomainKey newObj = (DomainKey) arg0;
			return this.toString().equals(newObj.toString());
		}
		return super.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getKey().toString();
	}
	
	

}
