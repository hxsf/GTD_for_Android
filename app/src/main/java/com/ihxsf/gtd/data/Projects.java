package com.ihxsf.gtd.data;

/**
 * Created by hxsf on 16－06－01.
 */
public enum Projects {
    Inbox,
    Next,
    Watch,
    Future;
    public static int getPostion(Projects projects){
        switch (projects) {
            case Inbox:return 1;
            case Next:return 2;
            case Watch:return 3;
            case Future:return 4;
            default:return 1;
        }
    }
    public static Projects getValue(int postion){
        switch (postion) {
            case 1:return Inbox;
            case 2:return Next;
            case 3:return Watch;
            case 4:return Future;
            default:return Inbox;
        }
    }
}
