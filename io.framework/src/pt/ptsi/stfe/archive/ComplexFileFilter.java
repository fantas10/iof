/**
 * Serviço Factura Electrónica
 * PT – Sistemas de Informação, S.A. 
 * 
 * io.framework
 * 2011/03/30
 */
package pt.ptsi.stfe.archive;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.SizeFileFilter;

import pt.ptsi.stfe.io.engine.PropertyMap;

import static pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.SizeOrientation;
import static pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.AgeOrientation;
import static pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.AgeType;
import static pt.ptsi.stfe.io.engine.xml.IOXmlFilterOptions.PROPERTIES.*;

/**
 * XML Implementation example<br>
 * 			<options>
 *				<gauge>5</gauge>
 * 				<filter>^(\w+)_(\w+)_504818180.xml$</filter>
 *				<size type="atLeast|atMost">15</size> <!-- in Kb -->
 * 				<age type="created|modified" unit="sec|min|hour|day|week|month|year">5</age>
 *			</options>
 * 
 * Size is in KBytes
 * 
 * 
 * 
 * @author Nuno P. Lourenço <nuno-p-lourenco@telecom.pt>
 *  Direcção de Exploração - Serviço de Factura Electrónica
 *  www.ptsi.pt
 *
 */
public class ComplexFileFilter extends AbstractFileFilter  {
	
	private String regexFilter = null;
	
	private long size = -1L;
	private SizeOrientation sizeOrientation = SizeOrientation.atLeast;
	private int age = -1;
	private AgeType ageType = AgeType.created;
	private AgeOrientation ageOrientation = AgeOrientation.olderThan;
	

	
	
	/**
	 * All variables are optional
	 * 
	 * @param config
	 */
	public ComplexFileFilter(final PropertyMap config) {
		
		// config vars
		if (config.contains(FILTER)) {
			setRegexFilter(config.getString(FILTER));
		}
		if (config.contains(SIZE)) {
			setSize(config.getLong(SIZE));
		}
		if (config.contains(SIZEDIRECTION)) {
			setSizeOrientation( (SizeOrientation) config.getEnumObject(SIZEDIRECTION) );
		}
		if (config.contains(AGE)) {
			setAge(config.getInt(AGE));
		}
		if (config.contains(AGETYPE)) {
			setAgeType((AgeType)config.getEnumObject(AGETYPE));
		}
		if (config.contains(AGEDIRECTION)) {
			setAgeOrientation((AgeOrientation) config.getEnumObject(AGEDIRECTION));
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.apache.commons.io.filefilter.IOFileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		boolean allFilters = true;
		
		if (getRegexFilter() != null && !getRegexFilter().isEmpty()) {
			RegexFileFilter regex = new RegexFileFilter(getRegexFilter().trim());
			allFilters &= regex.accept(file);
		}
		
		if (getSize() != -1L) {
			IOFileFilter sizeFilter = null;
			if (getSize() == 0) {
				sizeFilter = FileFilterUtils.sizeRangeFileFilter(0, 0);
			} else {
				sizeFilter = new SizeFileFilter((getSize()/1024), getSizeOrientation().equals(SizeOrientation.atLeast));
			}

			allFilters &= sizeFilter.accept(file);
		}
		
		if (getAge() != -1L) {
			boolean olderThan = getAgeOrientation().equals(AgeOrientation.olderThan);
			Calendar calendarInstance = Calendar.getInstance(); // current time
			calendarInstance.add(Calendar.SECOND, -getAge());  // subtracts in age
			Date cutoffDate = calendarInstance.getTime(); // date cut from age specified
			
			AgeFileFilter ageFilter = new AgeFileFilter(cutoffDate, olderThan);
			allFilters &= ageFilter.accept(file);
		}
		
		return allFilters;
	}

	/**
	 * @return the regexFilter
	 */
	public String getRegexFilter() {
		return regexFilter;
	}

	/**
	 * @param regexFilter the regexFilter to set
	 */
	public void setRegexFilter(String regexFilter) {
		this.regexFilter = regexFilter;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the sizeOrientation
	 */
	public SizeOrientation getSizeOrientation() {
		return sizeOrientation;
	}

	/**
	 * @param sizeOrientation the sizeOrientation to set
	 */
	public void setSizeOrientation(SizeOrientation sizeOrientation) {
		this.sizeOrientation = sizeOrientation;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the ageType
	 */
	public AgeType getAgeType() {
		return ageType;
	}

	/**
	 * @param ageType the ageType to set
	 */
	public void setAgeType(AgeType ageType) {
		this.ageType = ageType;
	}

	/**
	 * @return the ageOrientation
	 */
	public AgeOrientation getAgeOrientation() {
		return ageOrientation;
	}

	/**
	 * @param ageOrientation the ageOrientation to set
	 */
	public void setAgeOrientation(AgeOrientation ageOrientation) {
		this.ageOrientation = ageOrientation;
	}
}
