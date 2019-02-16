def call(def config){

    echoColor 'Paramètres :\n' + config.collect { "\t$it.key : $it.value" }.join('\n'), 'bleu'

    echoColor 'Version des outils :', 'bleu'

    def javaHome = tool config.javaVersion
    def mvnHome = tool config.mavenVersion

    withEnv(["PATH+MAVEN=${mvnHome}/bin", "JAVA_HOME=${javaHome}", "PATH+JDK=${javaHome}/bin"]) {
        sh "java -version"
        sh "mvn --version"
    }
}
