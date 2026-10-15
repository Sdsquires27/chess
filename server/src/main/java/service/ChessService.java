package service;

import dataaccess.DataAccess;

public abstract class ChessService {

    final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess) {
    this.dataAccess = dataAccess;
}
}
