package com.carlyu.pmxv.local.annotation

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class StandardClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class InsecureClient