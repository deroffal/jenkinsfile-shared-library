def call(def cmd, def config) {
    def javaHome = tool config.javaVersion
    def mvnHome = tool config.mavenVersion

    withEnv(["PATH+MAVEN=${mvnHome}/bin", "JAVA_HOME=${javaHome}", "PATH+JDK=${javaHome}/bin"]) {
        sh "mvn -B $cmd"
    }
}

def build(def config) {
    call 'clean install -DskipTests', config
}

def test(def config) {
    call 'surefire:test -DtestFailureIgnore=true', config
}

def integrationTest(def config) {
    call 'failsafe:integration-test -DskipAfterFailureCount=999', config
}