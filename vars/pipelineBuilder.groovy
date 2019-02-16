def call(String name, def config) {
    def jenkinsfile
    switch (name) {
        case 'maven':
            jenkinsfile = new fr.deroffal.pipeline.MavenContinuousIntegration()
            break
        default:
            throw new IllegalArgumentException('Pas encore implémenté!')
    }
    jenkinsfile.execute(config)
}
