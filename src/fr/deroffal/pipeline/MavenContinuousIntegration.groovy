package fr.deroffal.pipeline

def execute(def config) {
    node {

        setupBuild(config)

        stageLogged('Checkout') {
            deleteDir()
            checkout scm
        }

        stageLogged('Build') {
            mvn 'clean package -DskipTests', config
        }

        stageLogged('Tests') {
            if (!params.SkipTests) {
                parallel(
                        "unit": { mvn 'surefire:test -DtestFailureIgnore=true', config },
                        "integration": { mvn 'failsafe:integration-test -DskipAfterFailureCount=999', config }
                )
            }
        }

        stageLogged('SonarQube analysis') {
            if (env.getEnvironment().BRANCH_NAME == 'master') {
                withSonarQubeEnv('SonarDeroffal') {
                    mvn 'clean org.jacoco:jacoco-maven-plugin:prepare-agent package failsafe:integration-test sonar:sonar', config
                }
            }

        }

        stageLogged('Release') {
            if (params.Release) {
                withCredentials([
                        usernamePassword(credentialsId: 'GithubAccount', passwordVariable: 'gitPass', usernameVariable: 'gitUser'),
                        usernamePassword(credentialsId: 'GithubMail', passwordVariable: 'inutile', usernameVariable: 'gitEmail')]
                ) {
                    sh "git config user.name $gitUser"
                    sh "git config user.email $gitEmail"
                    sh "git checkout -B ${env.getEnvironment().BRANCH_NAME}"

                    mvn 'release:clean release:prepare', config
                }


            }
        }

    }
}


/**
 * Initialisation du build :
 * <ul>
 *     <li>Initialisation des paramètres du build</li>
 *     <li>Initialisation des paramètres du build</li>
 * </ul>
 */
private void setupBuild(def config) {
    properties([
            parameters([
                    booleanParam(name: 'SkipTests', description: "Permet de sauter l'éxecution des tests", defaultValue: false),
                    booleanParam(name: 'Release', description: "Release maven : incrémenter la version et créer un tag", defaultValue: false)
            ])
    ])

    echoColor 'Configuration du build :', "cyan"
    versions config

}