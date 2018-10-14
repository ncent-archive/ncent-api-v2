package kotlinserverless.framework.controllers

import kotlinserverless.main.daos.User

class DefaultRestController<T> : RestController<T, User>