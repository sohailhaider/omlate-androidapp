package com.sohailhaider.omlate;

/**
 * Created by Sohail Haider on 31-Dec-16.
 */
public class Variables {
    private static Variables mInstance= null;

    public String Username;
    public String FirstName;
    public String LastName;
    public String Email;
    public String PhoneNo;
    //
    public String LastClassID;
    public boolean LoggedIn;
    public CoursesListViewData LastSelectedCourse;
    public Assessment LastAssessment;
    public int LastQuizID;
    protected Variables(){}

    public static synchronized Variables getInstance(){
        if(null == mInstance){
            mInstance = new Variables();
        }
        return mInstance;
    }
}
