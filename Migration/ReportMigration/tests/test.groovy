import groovy.lang.GroovyClassLoader
import groovy.junit5.plugin.JUnit5Runner
// Cant figure out how to collect classes into a single test suite like the JUnit 4 style
// A Nested central test class is the workaround
GroovyClassLoader cloader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader())
new JUnit5Runner().run(cloader.parseClass(new File("StaticReportMigrationTest.groovy")), cloader)