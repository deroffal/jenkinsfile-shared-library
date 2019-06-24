package fr.deroffal.pipeline.tools

class Maven {

    //Jenkins Pipeline Steps
    def steps

    //Build configuration
    def config

    def build() {
        steps.mvn 'clean install -DskipTests', config
    }

    def test() {
        steps.mvn 'surefire:test -DtestFailureIgnore=true', config
    }

    def integrationTest() {
        steps.mvn 'failsafe:integration-test -DskipAfterFailureCount=999', config
    }

    def sonar(){
        steps.mvn 'clean org.jacoco:jacoco-maven-plugin:prepare-agent package failsafe:integration-test sonar:sonar', config
    }

    def release(){
        steps.mvn 'release:clean release:prepare', config
    }
}
