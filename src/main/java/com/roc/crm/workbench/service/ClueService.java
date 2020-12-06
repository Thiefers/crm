package com.roc.crm.workbench.service;

import com.roc.crm.vo.PaginationVO;
import com.roc.crm.workbench.domain.Clue;
import com.roc.crm.workbench.domain.Tran;

import java.util.Map;

public interface ClueService {

    boolean save(Clue clue);

    PaginationVO<Clue> pageList(Map<String, Object> map);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aids);

    boolean convert(String clueId, Tran t, String createBy);
}
