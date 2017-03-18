package com.Xxx_air.productlogsys.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionFactoryImplementor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.SessionFactoryUtils;

import com.Xxx_air.peanuts.dao.PeanutsDao;
import com.Xxx_air.productlogsys.dto.Logedit;
import com.Xxx_air.productlogsys.model.Mcp_report_fh;


@Named
public class LogEditDao   extends PeanutsDao{
	
	
	
	@Resource(name = "medetHRSessionFactory2")
	public void setSuperSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}
	 

    //调用存储过程mcp_ischange
    public ArrayList<Logedit> getChangeValue(final Date start,final Date end){
	    return (ArrayList<Logedit>)getHibernateTemplate().execute(
			 new HibernateCallback() {

				@Override
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					java.sql.Date starttime = new java.sql.Date(start.getTime());
					java.sql.Date endtime = new java.sql.Date(end.getTime());
					ArrayList<Logedit> list=new ArrayList<Logedit>();
					
					CallableStatement call =null;
					ResultSet rs =null;
					String sql ="{call monitorchangepls.mcp_ischange(?,?,?)}";
						try {
							call = SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection()
									.prepareCall(sql);
							call = session.connection().prepareCall(sql);
							call.setDate(1, starttime);
							call.setDate(2, endtime);
							call.registerOutParameter(1, OracleTypes.CURSOR);
							call.executeQuery();
							rs = ((OracleCallableStatement) call).getCursor(1);
							while(rs.next()){
								Logedit logedit=new Logedit();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
								logedit.setMsn(rs.getString(1));
								String  aogTime = sdf.format(rs.getDate(2));
								logedit.setAog_start_plan(aogTime);
								logedit.setAoginfoid_1(rs.getInt(3));
								
								list.add(logedit);
							}
						} catch (Exception e) {						
							e.printStackTrace();
						} finally {
									rs.close();
							    	call.close();
									session.close();
						}
					return list;
				}
			} 
			
	    );
    }
 
 
 
    //logview.jsp页面fh取值 2016/9/26 By：lhf
    public ArrayList<Mcp_report_fh> loadFh(final String sql) {	
	    return (ArrayList<Mcp_report_fh>) getHibernateTemplate().execute(
			new HibernateCallback() {

				@Override
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					
					List l = new ArrayList();
					ResultSet rs = null;

					CallableStatement pst = null;
					try {
						pst = session.connection().prepareCall(sql);
						rs = pst.executeQuery();
						
						while (rs.next()) {
							Mcp_report_fh mrf = new Mcp_report_fh();    
							mrf.setFH(rs.getFloat(1));
							mrf.setFH_PLAN(rs.getFloat(2));
							mrf.setFH_EARLIEST(rs.getFloat(3));
							mrf.setMSN(rs.getString(4));
							l.add(mrf);
						}
					} catch(Exception e){
						e.printStackTrace();
					}finally{
						if(session!=null){
							if(session.isOpen()){
								session.close();
							}
						}
					}
					return l;
				}
	    });
    }

    public BigDecimal loadReportid(final String sql) {
	  return (BigDecimal) getHibernateTemplate().execute(new  HibernateCallback() {

		@Override
		public Object doInHibernate(Session session) throws HibernateException, SQLException {
			// TODO Auto-generated method stub
			BigDecimal reportid = null  ;  
			try {
				List list = session.createSQLQuery(sql).list();
				 reportid = (BigDecimal)list.get(0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if(session!=null){
					if(session.isOpen()){
						session.close();
					}
				}
			}
			return reportid;
		}
	});
}


    //往MCP_REPORT_FH表中插入数据
    public void insertValue(Object object)  throws HibernateException{
	  getHibernateTemplate().saveOrUpdate(object);  
	}





	 //从提供的reportid中获取数据
	public ArrayList<Logedit> getInfo(final String sql,final String report_id) {
		// TODO Auto-generated method stub
		
		return(ArrayList<Logedit>)getHibernateTemplate().execute(
				new HibernateCallback(){
					@Override
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						ArrayList<Logedit> list=new ArrayList<Logedit>();
						CallableStatement pStatement=null;
						ResultSet rSet=null;
						try {
							pStatement=SessionFactoryUtils.getDataSource(getSessionFactory()).getConnection().prepareCall(sql);
							rSet=pStatement.executeQuery();
							while(rSet.next()){
								Logedit logedit=new Logedit();
								logedit.setAog_start_plan(rSet.getString(1));
								logedit.setMsn(rSet.getString(2));
								logedit.setAoginfoid_1(rSet.getInt(3));
								logedit.setReportid_2(report_id);
								list.add(logedit);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}finally{
							rSet.close();
							pStatement.close();
							session.close();
						}
					
						return list;
					}
					
				}	
			);
	}

}