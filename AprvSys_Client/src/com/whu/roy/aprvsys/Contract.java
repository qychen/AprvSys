package com.whu.roy.aprvsys;

import java.io.Serializable;

public class Contract implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int status, sum;
	public String title, author, content, time;
	public String number, category, pronum, proname, goodnum, goodname;
	public String sum_cat, rate, payment, signname, dealname, period, site, punish, ps;
	public int Id;
	public String comment;
	
	public Contract() {}
	
	public Contract(int s, String title, String au, String con, String time)
	{
		this.status = s;
		this.title =title;
		this.author = au;
		this.content = con;
		this.time = time;
	}
	
	public Contract(int s, String title, String au, String con, String time, 
			int sum, String number, String category, String pronum, 
			String proname, String goodnum, String goodname, 
			String sum_cat, String rate, String payment, String signname, 
			String dealname, String period, String site, String punish, String ps)
	{
		this.status = s;
		this.title =title;
		this.author = au;
		this.content = con;
		this.time = time;
		this.sum = sum;
		this.number = number;
		this.category = category;
		this.pronum = pronum;
		this.proname = proname;
		this.goodnum = goodnum;
		this.goodname = goodname;
		this.sum_cat = sum_cat;
		this.rate = rate;
		this.payment = payment;
		this.signname = signname;
		this.dealname = dealname;
		this.period = period;
		this.site = site;
		this.punish = punish;
		this.ps = ps;
	}
	
}
