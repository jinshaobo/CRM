package com.jinshaobo.crm.workbench.service;


import com.jinshaobo.crm.workbench.domain.Clue;
import com.jinshaobo.crm.workbench.domain.Tran;


public interface ClueService {

    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);


    boolean convert(String clueId, Tran tran, String createBy);
}
