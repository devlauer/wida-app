package de.elnarion.web.wida.metadataservice.domain.typemetadata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import de.elnarion.web.wida.metadataservice.WidaMetaDataConstants;

/**
 * This class is used to sync the different structure models in a cluster.
 */
@Entity
@Table(name = WidaMetaDataConstants.METADATA_TYPE_VERSION_LOCK_TABLE, schema = WidaMetaDataConstants.METADATA_DB_SCHEMA)
public class VersionLock {

	/** The locktype. */
	private String locktype;
	
	/** The version. */
	private Long version=new Long(1);

	
	/**
	 * Instantiates a new version lock.
	 */
	public VersionLock() {
	}

	/**
	 * Gets the locktype.
	 *
	 * @return String - the locktype
	 */
	@Id
	@Column(name="locktype",length=30)
	public String getLocktype() {
		return locktype;
	}

	/**
	 * Sets the locktype.
	 *
	 * @param locktype
	 *            the locktype
	 */
	public void setLocktype(String locktype) {
		this.locktype = locktype;
	}

	/**
	 * Gets the version.
	 *
	 * @return Long - the version
	 */
	@Column(name="version")
	public Long getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version
	 *            the version
	 */
	public void setVersion(Long version) {
		this.version = version;
	}

	
}
