package kotlinserverless.framework.controllers

import kotlinserverless.main.users.models.User

class DefaultRestController<T> : RestController<T, User>