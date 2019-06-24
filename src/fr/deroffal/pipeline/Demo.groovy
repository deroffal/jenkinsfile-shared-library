package fr.deroffal.pipeline

import fr.deroffal.pipeline.tools.*

def execute(def config) {
    node {

        Maven mvn = new Maven(steps: this, config: config)

        stageLogged('Démo') {
            deleteDir()
            checkout scm
        }

        stageLogged('Build') {
            mvn.build()
        }
    }
}
