package kotlinserverless.framework.controllers

import main.daos.User

class DefaultRestController<T> : RestController<T, User>