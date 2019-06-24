import fr.deroffal.pipeline.*

def call(String name, def config) {
    findPipelineByName(name).execute config
}

private def findPipelineByName(String name) {
    switch (name) {
        case 'maven':
            return new MavenContinuousIntegration()
        case 'demo':
            return new Demo()
        default:
            throw new IllegalArgumentException('Pas encore implémenté!')
    }
}
