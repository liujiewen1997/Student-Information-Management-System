package com.ljw.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.ljw.dao.StudentDao;
import com.ljw.domain.Student;
import com.ljw.util.JDBCUtil02;
import com.ljw.util.TextUtil;

/**
 * 这是StudentDao的实现。针对前面定义的规范，做出具体的实现
 * @author Administrator
 *
 */
public class StudentDaoImpl implements StudentDao {
	
	
	@Override
	public List<Student> findAll() throws SQLException {
		QueryRunner runner = new QueryRunner(JDBCUtil02.getdataSource());
		String sql = "select * from stu";
		List<Student> list = runner.query(sql, new BeanListHandler<Student>(Student.class));
		return list;
	}

	@Override
	public void insert(Student student) throws SQLException {
		QueryRunner runner = new QueryRunner(JDBCUtil02.getdataSource());
		runner.update("insert into stu values(null,?,?,?,?,?,?)", 
				student.getSname(),
				student.getGender(),
				student.getPhone(),
				student.getBirthday(),
				student.getHobby(),
				student.getInfo()
				);
	}

	@Override
	public void delete(int sid) throws SQLException {
		QueryRunner runner = new QueryRunner(JDBCUtil02.getdataSource());
		runner.update("delete from stu where sid = ?", sid);
	}

	@Override
	public Student findstudentById(int sid) throws SQLException {
		QueryRunner runner = new QueryRunner(JDBCUtil02.getdataSource());
		return runner.query("select * from stu where sid=?", new BeanHandler<Student>(Student.class),sid);
		
		
		
		
	}

	@Override
	public void update(Student student) throws SQLException {
		QueryRunner runner = new QueryRunner(JDBCUtil02.getdataSource());
		runner.update("update stu set sname=?,gender=?,phone=?,birthday=?,hobby=?,info=? where sid=?", 
				student.getSname(),
				student.getGender(),
				student.getPhone(),
				student.getBirthday(),
				student.getHobby(),
				student.getInfo(),
				student.getSid());
	}

	@Override
	public List<Student> searchStudent(String sname, String sgender) throws SQLException {
		System.out.println("现在要执行模糊查询了，收到的name="+sname+"收到额Sgender"+sgender);
		QueryRunner runner = new QueryRunner(JDBCUtil02.getdataSource());
		
		/*
		 * 这里要分析一下：
		 * 	如果只有姓名 ，select * from stu where sname like ? ;
		 * 	如果只有性别 ， select * from stu where gender = ?
		 * 
		 * 如果两个都有 select * from stu where sname like ? and gender=?
		 * 
		 * 如果两个都没有就查询所有。
		 * 
		 */
		
		String sql = "select * from stu where 1=1 ";
		List<String> list = new ArrayList<>();
		
		//判断有没有姓名，如果有，就组拼到sql语句里面
		if (!TextUtil.isEmpty(sname)) {
			sql = sql+"and sname like ?";
			list.add("%"+sname+"%");
		}
		
		//判断有没有性别，有得话，就组拼到SQL语句中
		
		if(!TextUtil.isEmpty(sgender)){
			sql= sql+"and gender = ?";
			list.add(sgender);
		}
		System.out.println(sql);
		return runner.query(sql, new BeanListHandler<Student>(Student.class),list.toArray());
		
	}

	@Override
	public List<Student> findStudentByPage(int currentPage) throws SQLException {
		QueryRunner runner = new QueryRunner(JDBCUtil02.getdataSource());
		//第一个问号，代表一页返回多少条记录  ， 第二个问号， 跳过前面的多少条记录。
		//5 0 --- 第一页 (1-1)*5
		//5 5  --- 第二页 (2-1)*5
		//5 10  --- 第三页
		return runner.query("select * from stu limit ? offset ?", new BeanListHandler<Student>(Student.class),PAGE_SIZE,(currentPage-1)*PAGE_SIZE);
	}

	@Override
	public int findCount() throws SQLException {
		QueryRunner runner = new QueryRunner(JDBCUtil02.getdataSource());
		//ScalarHandler用于处理平均值，总的个数
		Long result = (Long) runner.query("select count(*) from stu", new ScalarHandler());
		return result.intValue();
	}



}
