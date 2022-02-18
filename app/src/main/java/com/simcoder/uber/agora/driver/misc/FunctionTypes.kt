package com.terlive.core.misc

typealias NoParamFunction = ()->Unit
typealias OneParamFunction<T> =(param:T)->Unit
typealias TypeMapper<T,R> = T.()->R
typealias TwoParamFunction<T,E> =(param1:T,param2:E)->Unit
typealias ThreeParamFunction<T,E,R> =(param1:T,param2:E,param3:R)->Unit