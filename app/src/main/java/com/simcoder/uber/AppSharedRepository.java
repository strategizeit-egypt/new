package com.simcoder.uber;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class AppSharedRepository {




          static String TERLIVE_APP = "TerliveApp";
          static String USER_TYPE = "UserType";
          String TOKEN = "Token";
        private  String ACCEPT ="Accept";
        private  String LANGUAGE = "Language";
        private  String LANGUAGE_ID ="LanguageID";
         static String USER ="User";
        static   String DRIVER ="Driver";
        private  String STUDENT = "Student";
        private  String STUDENTS = "Students";
        private  String CHILDREN = "Children";
        private  String GRADES ="Grades";
        private  String SUBJECTS ="Subjects";
        private  String GRADE ="Grade";
        private  String PORTAL ="Portal";
        private  String TOKEN_EXPIRATION_TIME = "TokenTime";

        private Gson gson = new Gson();
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
*//*
        static public saveUserType(userType :String){
            prefsEditor.putString(USER_TYPE, userType)
            prefsEditor.commit()
        }*/


   static public Boolean isDriver( Context context) {
            SharedPreferences sharedPreferences  = context.getSharedPreferences(
                    TERLIVE_APP, MODE_PRIVATE);
            return sharedPreferences.getBoolean(DRIVER, false);

        }
        static public void setIsDriver(Context context, Boolean isDriver) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    TERLIVE_APP, MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor= sharedPreferences.edit();

            prefsEditor.putBoolean(DRIVER, isDriver);
            prefsEditor.commit();
        }
        static public Boolean hasUserLogin(Context context){
            SharedPreferences sharedPreferences  = context.getSharedPreferences(
                    TERLIVE_APP, MODE_PRIVATE);
            return  sharedPreferences.getBoolean(USER, false);

        }
         static public void setUserLogin (Context context, Boolean isLogin ){
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    TERLIVE_APP, MODE_PRIVATE);
             SharedPreferences.Editor prefsEditor  = sharedPreferences.edit();
            prefsEditor.putBoolean(USER, isLogin);
            prefsEditor.commit();
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

        static public void  logOut(Context context) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    TERLIVE_APP, MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor =  sharedPreferences.edit();
          //  prefsEditor.remove(TOKEN)
            prefsEditor.remove(USER_TYPE);
          //  prefsEditor.remove(STUDENT)
            prefsEditor.remove(USER);
            prefsEditor.remove(DRIVER);
       /*     prefsEditor.remove(PORTAL)
            prefsEditor.remove(TOKEN_EXPIRATION_TIME)
            prefsEditor.remove(STUDENTS)
            prefsEditor.remove(CHILDREN)*/
            prefsEditor.commit();
        }

/*
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
