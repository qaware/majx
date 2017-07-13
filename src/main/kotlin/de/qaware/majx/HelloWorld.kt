package de.qaware.majx

class HelloWorld {

    fun sayHello(formal: Boolean): String {
        if (formal) {
            return "Hello, ${getWorld()}!"
        } else {
            return "Cheers, ${getWorld()}!"
        }
    }
}

