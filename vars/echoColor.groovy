def call(def msg, String color) {
    ansiColor('xterm') {
        echo "${colorCodeOf(color)}${msg}${noir}"
    }
}


String colorCodeOf(String color) {
    switch (color) {
        case 'cyan': return cyan
        case 'magenta': return magenta
        case 'vert': return vert
        case 'bleu': return bleu
        case 'rouge': return rouge
        default: return noir
    }
}

def getRouge() { "\u001B[31m" }

def getMagenta() { "\u001B[35m" }

def getVert() { "\u001B[32m" }

def getCyan() { "\u001B[36m" }

def getBleu() { "\u001B[34m" }

def getNoir() { "\u001B[0m" }