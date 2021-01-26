package cn.x5456.initializer.db;

/**
 * 支持的 {@link javax.sql.DataSource} 初始化模式。
 *
 * 仿造 {@link org.springframework.boot.jdbc.AbstractDataSourceInitializer}，建议先看一下 Spring 的实现
 */
public enum DataSourceInitializationMode {

	/**
	 * Always initialize the datasource.
	 */
	ALWAYS,

	/**
	 * Do not initialize the datasource.
	 */
	NEVER

}
