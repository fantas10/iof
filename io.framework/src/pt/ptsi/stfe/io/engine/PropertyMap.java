/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/04/02
 */
package pt.ptsi.stfe.io.engine;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A generic <code>Map</code> for <code>Enum</code> type keys
 * 
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class PropertyMap implements Serializable {
	
	private final Map<String, Object> internalData;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public PropertyMap() {
		internalData = new HashMap<String, Object>();
	}
	
	/**
	 * 
	 * @param m
	 */
	public PropertyMap(Map<? extends String, ? extends Object> m) {
		internalData = new HashMap<String, Object>();
		internalData.putAll(m);
	}
	
	/**
	 * 
	 * @param e
	 * @param o
	 */
	public void consume(Enum<?> e, Object o) {
		this.internalData.put(e.name(), o);
	}
	
	/**
	 * 
	 * @param m
	 */
	public void consumeAll(Map<? extends String, ? extends Object> m) {
		this.internalData.putAll(m);
	}
	
	/**
	 * 
	 * @param addMap
	 */
	public void consumeAll(PropertyMap addMap) {
		if (addMap != null && !addMap.internalData.isEmpty()) {
			this.internalData.putAll(addMap.internalData);
		}
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public boolean contains(Enum<?> e) {
		return this.internalData.containsKey(e.name());
	}
	
	/**
	 * 
	 * @param e
	 * @return
	 */
	public Object get(Enum<?> e) {
		return this.internalData.get(e.name());
	}
	
    /**
     * <p>
     * Retrieve the identified <code>String</code> value from the <code>PropertyMap</code>.
     * </p>
     * 
     * @throws ClassCastException
     *           if the identified object is not a String.
     */
    public String getString(Enum<?> e) {
        Object obj = get(e);
    
        try {
            return (String) obj;
        } catch (Exception e1) {
            throw new ClassCastException("Identified object is not a String.");
        }
    }
    
    /**
     * <p>
     * Retrieve the identified <code>long</code> value from the <code>PropertyMap</code>.
     * </p>
     * 
     * @throws ClassCastException
     *           if the identified object is not a Long.
     */
    public long getLong(Enum<?> e) {
        Object obj = get(e);
    
        try {
        	if(obj instanceof Long)
        		return ((Long) obj).longValue();
        	return Long.parseLong((String)obj);
        } catch (Exception e1) {
            throw new ClassCastException("Identified object is not a Long.");
        }
    }
    
    /**
     * <p>
     * Retrieve the identified <code>int</code> value from the <code>PropertyMap</code>.
     * </p>
     * 
     * @throws ClassCastException
     *           if the identified object is not an Integer.
     */
    public int getInt(Enum<?> e) {
        Object obj = get(e);
    
        try {
        	if(obj instanceof Integer)
        		return ((Integer) obj).intValue();
        	return Integer.parseInt((String)obj);
        } catch (Exception e1) {
            throw new ClassCastException("Identified object is not an Integer.");
        }
    }
    
    /**
     * <p>
     * Retrieve the identified <code>boolean</code> value from the <code>PropertyMap</code>.
     * </p>
     * 
     * @throws ClassCastException
     *           if the identified object is not a Boolean.
     */
    public boolean getBoolean(Enum<?> key) {
        Object obj = get(key);
    
        try {
        	if(obj instanceof Boolean)
        		return ((Boolean) obj).booleanValue();
        	return Boolean.parseBoolean((String)obj);
        } catch (Exception e) {
            throw new ClassCastException("Identified object is not a Boolean.");
        }
    }
    
    /**
     * <p>
     * Retrieve the identified <code>Date</code> value from the <code>PropertyMap</code>.
     * </p>
     * 
     * @param key
     * 
     * @throws ClassCastException
     *           if the identified object is not a Date.
     *           
     * @return the <code>Date</code> object
     */
    public Date getDate(Enum<?> key) {
    	Object obj = get(key);
    	//
    	try {
    		if (obj instanceof java.util.Date) {
    			return ((Date) obj);
    		} else {
    			return null;
    		}    		
    	} catch (Exception e) {
    		throw new ClassCastException("Identified object is not a java.util.Date.");
    	}
    }
    
    /**
     * <p>
     * Retrieve the identified <code>Enum</code> object from the <code>PropertyMap</code>.
     * </p>
     * 
     * @throws ClassCastException
     *           if the identified object is not a Enum.
     */
	public Enum<?> getEnumObject(Enum<?> e) {
        Object obj = get(e);
    
        try {
        	return ((Enum<?>) obj);
        } catch (Exception e1) {
            throw new ClassCastException("Identified object is not an Enum.");
        }
    }

}
