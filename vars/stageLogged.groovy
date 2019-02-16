def call(String nom, Closure closure) {
    stage(nom) {
        echoColor "Etape $nom", 'magenta'
        closure.call()
    }
}