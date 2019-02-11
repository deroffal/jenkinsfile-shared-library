def call(def cmd, def config) {
    def javaHome = tool config.javaVersion
    def mvnHome = tool config.mavenVersion

    withEnv(["PATH+MAVEN=${mvnHome}/bin", "JAVA_HOME=${javaHome}", "PATH+JDK=${javaHome}/bin"]) {
        sh "mvn -B $cmd"
    }
}