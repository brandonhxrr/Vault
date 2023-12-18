package com.brandonhxrr.vault.data

class User {
    var name: String = ""
    var email: String = ""
    var photoURL: String = ""
    var publicKey: String = ""

    constructor()

    constructor(name: String, email: String, photoURL: String, publicKey: String) {
        this.name = name
        this.email = email
        this.photoURL = photoURL
        this.publicKey = publicKey
    }
}