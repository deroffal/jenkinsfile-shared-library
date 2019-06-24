package fr.deroffal.pipeline

import fr.deroffal.pipeline.tools.Maven

def execute(def config) {
    node {

        setupBuild(config)

        Maven mvn = new Maven(steps: this, config: config)

        stageLogged('Checkout') {
            deleteDir()
            checkout scm
        }

        stageLogged('Build') {
            mvn.build()
        }

        stageLogged('Tests unitaires') {
            if (!params.SkipTests) {
                mvn.test()
            }
        }

        stageLogged("Tests d'intégration") {
            if (!params.SkipTests) {
                mvn.integrationTest()
            }
        }

        stageLogged('Analyse SonarQube') {
            if (env.getEnvironment().BRANCH_NAME == 'master') {
                withSonarQubeEnv('SonarDeroffal') {
                    mvn.sonar()
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

                    mvn.release()
                }


            }
        }

    }
}


/**
 * Initialisation du build :
 * <ul>
 *     <li>Initialisation des paramètres du build</li>
 *     <li>Affichage des versions des outils</li>
 * </ul>
 */
private void setupBuild(def config) {
    properties([
            parameters([
                    booleanParam(name: 'SkipTests', description: "Ne pas exécuter les tests", defaultValue: false),
                    booleanParam(name: 'Release', description: "Release Maven : incrémenter la version et créer un tag", defaultValue: false)
            ])
    ])

    versions config
}