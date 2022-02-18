package com.simcoder.uber

import android.content.Context
import com.google.gson.Gson
import android.content.SharedPreferences
import com.simcoder.uber.AppSharedRepository

class AppSharedRepository {
    var TOKEN = "Token"
    private val ACCEPT = "Accept"
    private val LANGUAGE = "Language"
    private val LANGUAGE_ID = "LanguageID"
    private val STUDENT = "Student"
    private val STUDENTS = "Students"
    private val CHILDREN = "Children"
    private val GRADES = "Grades"
    private val SUBJECTS = "Subjects"
    private val GRADE = "Grade"
    private val PORTAL = "Portal"
    private val TOKEN_EXPIRATION_TIME = "TokenTime"
    private val gson = Gson()

    companion object {
        var TERLIVE_APP = "TerliveApp"
        var USER_TYPE = "UserType"
        var USER = "User"
        var DRIVER = "Driver"

        /*     private String sharedPreferences by lazy {  MeshwarApplication.instance!!.getSharedPreferences(
                TERLIVE_APP, MODE_PRIVATE)}
        private String prefsEditor: SharedPreferences.Editor by lazy {  sharedPreferences.edit()}

*/
        /*
        static public getUser(context :Context): CustomerObject {
            String sharedPreferences by lazy {  context.getSharedPreferences(
                    TERLIVE_APP, MODE_PRIVATE)}
            String prefsEditor: SharedPreferences.Editor by lazy {  sharedPreferences.edit()}
            String json = sharedPreferences.getString(USER, null)
            return gson.fromJson(json, CustomerObject::class.java)
        }
        static public getDriver(): DriverObject {
            String json = sharedPreferences.getString(DRIVER, null)
            return gson.fromJson(json, DriverObject::class.java)
        }*/
        /*

    static public saveGrade(gradeUI: GradeUI?){
        prefsEditor.putString(GRADE, gradeUI?.toStringData())
        prefsEditor.commit()
    }

    static public getGrade(): GradeUI? {
        String json = sharedPreferences.getString(GRADE, null)
        return gson.fromJson(json, GradeUI::class.java)
    }
*/
        /*    static public saveUserData(userEntity: CustomerObject){

            prefsEditor.putString(USER, userEntity.toString())
            prefsEditor.commit()
        }
        static public saveDriver(userEntity: DriverObject){
            prefsEditor.putString(DRIVER, userEntity.toString())
            prefsEditor.commit()
        }
*/
        /*
        static public saveUserType(userType :String){
            prefsEditor.putString(USER_TYPE, userType)
            prefsEditor.commit()
        }*/
        fun isDriver(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(
                TERLIVE_APP, Context.MODE_PRIVATE
            )
            return sharedPreferences.getBoolean(DRIVER, false)
        }

        fun setIsDriver(context: Context, isDriver: Boolean?) {
            val sharedPreferences = context.getSharedPreferences(
                TERLIVE_APP, Context.MODE_PRIVATE
            )
            val prefsEditor = sharedPreferences.edit()
            prefsEditor.putBoolean(DRIVER, isDriver!!)
            prefsEditor.commit()
        }

        fun hasUserLogin(context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(
                TERLIVE_APP, Context.MODE_PRIVATE
            )
            return sharedPreferences.getBoolean(USER, false)
        }

        fun setUserLogin(context: Context, isLogin: Boolean?) {
            val sharedPreferences = context.getSharedPreferences(
                TERLIVE_APP, Context.MODE_PRIVATE
            )
            val prefsEditor = sharedPreferences.edit()
            prefsEditor.putBoolean(USER, isLogin!!)
            prefsEditor.commit()
        }

        /*    static public saveGrades(grades:List<Grade>){
        prefsEditor.putString(GRADES, grades.toStringData())
        prefsEditor.commit()
    }
    static public getGrades(): List<Grade>? {
        String json = sharedPreferences.getString(GRADES, null)
        //  json?.toObject<MutableList<Country>>()
        String type = object : TypeToken<MutableList<Grade>>() {}.type
        return gson.fromJson(json, type) as List<Grade>?
    }

    static public saveSubjects(grades:List<Subject>){
        prefsEditor.putString(SUBJECTS, grades.toStringData())
        prefsEditor.commit()
    }
    static public getSubjects(): List<Subject>? {
        String json = sharedPreferences.getString(SUBJECTS, null)
        String type = object : TypeToken<MutableList<Subject>>() {}.type
        return gson.fromJson(json, type) as List<Subject>?
    }


    static public getSelectedStudent(): StudentEntity? {
        String json = sharedPreferences.getString(STUDENT, null)
        json?.let {
            return gson.fromJson(it, StudentEntity::class.java)
        }?: run {
            return  null
        }

    }

    static public getSelectedCommunity(): CommunityUI? {
        String json = sharedPreferences.getString(PORTAL, null)
        json?.let {
            return gson.fromJson(it, CommunityUI::class.java)
        }?: run {
            return  null
        }
    }

    static public saveSelectedCommunity(communityUI: CommunityUI){
        prefsEditor.putString(PORTAL, communityUI.toStringData())
        prefsEditor.commit()
    }


    static public saveSelectedStudent(studentEntity: StudentEntity){
        prefsEditor.putString(STUDENT, studentEntity.toStringData())
        prefsEditor.commit()
    }


    static public getLanguageID(): String {
        String json = sharedPreferences.getString(LANGUAGE, null)
        String obj: AppLanguage = gson.fromJson(json, AppLanguage::class.java) ?: return "en"
        return obj.id
    }

    static public saveLanguage(language: AppLanguage) {
        prefsEditor.putString(LANGUAGE, language.toStringData())
        prefsEditor.commit()
    }

    static public getLanguage(): AppLanguage? {
        String json = sharedPreferences.getString(LANGUAGE, null)
        return gson.fromJson(json, AppLanguage::class.java)
    }

    static public getLanguageID(context :Context): String {
        String sharedPreferences = context.getSharedPreferences(TERLIVE_APP, MODE_PRIVATE)!!
        String json = sharedPreferences.getString(LANGUAGE, null)
        String obj: AppLanguage = gson.fromJson(json, AppLanguage::class.java) ?: return "en"
        return obj.id
    }

    static public setUserToken(token: String) {
        prefsEditor.putString(TOKEN, token)
        prefsEditor.commit()
    }

    static public getUserToken(): String {
        return sharedPreferences?.getString(TOKEN, "")!!
    }

    static public saveTokenExpiration(timeInMilliSecond: Long) {
        prefsEditor.putLong(TOKEN_EXPIRATION_TIME, timeInMilliSecond)
        prefsEditor.commit()
    }

    static public getTokenExpiration(): Long {
        return sharedPreferences?.getLong(TOKEN_EXPIRATION_TIME, 0)?:0
    }

    static public setUserType(userType: UserType) {
        prefsEditor.putString(USER_TYPE, userType.name)
        prefsEditor.commit()
    }

    static public getUserType(): UserType {
        return when (sharedPreferences?.getString(USER_TYPE, "")!!){
           UserType.PARENT.name ->  UserType.PARENT
            UserType.STUDENT.name ->  UserType.STUDENT
           UserType.TEACHER.name ->  UserType.TEACHER
           UserType.SUPERVISOR.name ->  UserType.SUPERVISOR
           else -> UserType.PARENT
       }
    }


    static public isLogin(): Boolean {
        return sharedPreferences.getString(USER_TYPE, "")?.isNotEmpty()!!
    }*/
        fun logOut(context: Context) {
            val sharedPreferences = context.getSharedPreferences(
                TERLIVE_APP, Context.MODE_PRIVATE
            )
            val prefsEditor = sharedPreferences.edit()
            //  prefsEditor.remove(TOKEN)
            prefsEditor.remove(USER_TYPE)
            //  prefsEditor.remove(STUDENT)
            prefsEditor.remove(USER)
            prefsEditor.remove(DRIVER)
            /*     prefsEditor.remove(PORTAL)
            prefsEditor.remove(TOKEN_EXPIRATION_TIME)
            prefsEditor.remove(STUDENTS)
            prefsEditor.remove(CHILDREN)*/prefsEditor.commit()
        } /*
    static public saveAccept(accept: Boolean) {
        prefsEditor.putBoolean(ACCEPT, accept)
        prefsEditor.commit()
    }


    static public isAcceptTerms(): Boolean {
        return sharedPreferences?.getBoolean(ACCEPT,false)?:false
    }

    static public saveStudents(list: List<StudentEntity>) {
        prefsEditor.putString(STUDENTS, list.toStringData())
        prefsEditor.commit()
    }
    static public saveChildren(list: List<StudentEntity>) {
        prefsEditor.putString(CHILDREN, list.toStringData())
        prefsEditor.commit()
    }

    static public getStudents(): List<StudentEntity>? {
        String json = sharedPreferences.getString(STUDENTS, null)
        String type = object : TypeToken<MutableList<StudentEntity>>() {}.type
        return gson.fromJson(json, type) as List<StudentEntity>?
    }

    static public getChildren(): List<StudentEntity>? {
        String json = sharedPreferences.getString(CHILDREN, null)
        String type = object : TypeToken<MutableList<StudentEntity>>() {}.type
        return gson.fromJson(json, type) as List<StudentEntity>?
    }
*/
    }
}


fun Any.toStringData(): String {
    return Gson().toJson(this)
}


inline fun <reified T> String.toObject(): T {
    return Gson().fromJson(this, T::class.java)
}