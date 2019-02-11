def call(def config) {
    pipeline {
        agent any

        parameters {
            booleanParam(defaultValue: false, description: 'Skip tests', name: 'SkipTests')
            booleanParam(defaultValue: false, description: 'Perform maven release', name: 'Release')
        }

        stages {
            stage('Checkout') {
                steps {
                    echoCouleur 'Checkout', 'rouge'
                    deleteDir()
                    checkout scm
                }
            }
            stage('Build') {
                steps {
                    echoCouleur 'Build', 'rouge'
                    mvn 'clean install -DskipTests', config
                }
            }
            stage('Tests') {
                when { expression { !params.SkipTests } }
                steps {
                    parallel(
                            "unit": {
                                echoCouleur 'Unit Tests', 'rouge'
                                mvn 'surefire:test -DtestFailureIgnore=true', config
                            },
                            "integration": {
                                echoCouleur 'Integration Tests', 'rouge'
                                mvn 'failsafe:integration-test -DskipAfterFailureCount=999', config
                            }
                    )
                }
            }
            stage('SonarQube analysis') {
                when { branch 'master' }
                steps {
                    echoCouleur 'SonarQube analysis', 'rouge'
                    withSonarQubeEnv('SonarDeroffal') {
                        mvn 'clean org.jacoco:jacoco-maven-plugin:prepare-agent package failsafe:integration-test sonar:sonar', config
                    }
                }
            }
            stage('Release') {
                when { expression { params.Release } }
                environment {
                    CREDENTIALS_NAME = credentials('GithubAccount')
                    CREDENTIALS_EMAIL = credentials('GithubMail')
                }
                steps {
                    echoCouleur 'Release', 'rouge'
                    git "config user.email \"$CREDENTIALS_NAME_USR\""
                    git "config user.name \"$CREDENTIALS_EMAIL_USR\""
                    git "checkout -B ${env.BRANCH_NAME}"
                    mvn 'release:clean release:prepare', config
                }
            }
        }
    }
}
