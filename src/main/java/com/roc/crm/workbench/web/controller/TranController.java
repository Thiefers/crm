package com.roc.crm.workbench.web.controller;

import com.roc.crm.settings.domain.User;
import com.roc.crm.settings.service.UserService;
import com.roc.crm.settings.service.impl.UserServiceImpl;
import com.roc.crm.utils.DateTimeUtil;
import com.roc.crm.utils.PrintJson;
import com.roc.crm.utils.ServiceFactory;
import com.roc.crm.utils.UUIDUtil;
import com.roc.crm.vo.PaginationVO;
import com.roc.crm.workbench.domain.Activity;
import com.roc.crm.workbench.domain.Clue;
import com.roc.crm.workbench.domain.Tran;
import com.roc.crm.workbench.domain.TranHistory;
import com.roc.crm.workbench.service.ActivityService;
import com.roc.crm.workbench.service.ClueService;
import com.roc.crm.workbench.service.CustomerService;
import com.roc.crm.workbench.service.TranService;
import com.roc.crm.workbench.service.impl.ActivityServiceImpl;
import com.roc.crm.workbench.service.impl.ClueServiceImpl;
import com.roc.crm.workbench.service.impl.CustomerServiceImpl;
import com.roc.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到交易控制器");
        String path = request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)) {
            add(request,response);
        } else if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request,response);
        } else if ("/workbench/transaction/save.do".equals(path)) {
            save(request,response);
        } else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request,response);
        } else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)) {
            getHistoryListByTranId(request,response);
        } else if ("/workbench/transaction/changeStage.do".equals(path)) {
            changeStage(request,response);
        } else if ("/workbench/transaction/getCharts.do".equals(path)) {
            getCharts(request,response);
        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得交易阶段数量统计图表的数据");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        /*
            业务层为我们返回
                total
                dataList
                通过map打包以上两项进行返回
         */
        Map<String,Object> map = tranService.getCharts();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行改变阶段的操作");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectDate = request.getParameter("expectDate");
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = tranService.changeStage(t);

        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("success",flag);
        map.put("t",t);

        PrintJson.printJsonObj(response,map);
    }

    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据交易id取得相应的历史列表");
        String tranId = request.getParameter("tranId");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> thList = tranService.getHistoryListByTranId(tranId);
        // 阶段和可能性之间的对应关系
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        // 将交易历史列表遍历
        for (TranHistory th : thList) {
            // 根据每条交易历史，取出每一个阶段
            String stage = th.getStage();
            String possibility = pMap.get(stage);
            th.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,thList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
        System.out.println("跳转到详细信息页");
        String id = request.getParameter("id");
        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t = tranService.detail(id);
        // 处理可能性
        /*
            阶段 t
            阶段和可能性之间的对应关系 pMap
         */
        String stage = t.getStage();
        // 三种选其一，都可取application
        // this.getServletContext()
        // this.getServletConfig().getServletContext()
        Map<String,String> pMap = (Map<String,String>)request.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException {
        System.out.println("执行交易添加的操作");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName"); // 此处暂时只有客户名称，没有id
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran tran = new Tran();
        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);

        TranService tranService = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = tranService.save(tran,customerName);
        if (flag) {
            // 如果添加交易成功，跳转到列表页
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得客户名称列表（根据客户名称进行模糊查询）");
        String name = request.getParameter("name");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> sList = customerService.getCustomerName(name);
        PrintJson.printJsonObj(response,sList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转至交易添加页的操作");
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = userService.getUserList();
        request.setAttribute("uList",uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }

}
