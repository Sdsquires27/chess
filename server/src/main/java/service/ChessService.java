package service;

import dataaccess.DataAccess;

public abstract class ChessService {

    final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
    }

    boolean invalidInput(String[] request){
        for (var field : request){
            if (field == null || field.isEmpty()){
                return true;
            }
        }
        return false;
    }
}
