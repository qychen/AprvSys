using System;
using System.Collections.Generic;
//using System.Linq;
using System.Text;
//using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Web.Script.Serialization;
//using System.Net.Configuration;
//using System.Configuration;
using System.Data;
using System.Data.SqlClient;

[Serializable]
public class SocketRequest
{
    public string Op { get; set; }

    //public string DateTime { get; set; }

    public object RequestData { get; set; }
}

[Serializable]
public class SocketResponse
{
    public string Op { get; set; }

    //public string DateTime { get; set; }

    public object ResultData { get; set; }
}

[Serializable]
public class Contract
{    
	public int status, sum, id;
	public String title, author, content, time;
	public String number, category, pronum, proname, goodnum, goodname;
	public String sum_cat, rate, payment, signname, dealname, period, site, punish, ps, comment;
}

[Serializable]
public class LoginResponse
{
    public Contract[] AprvConList, UpldConList;
    public string Email, RealName, Company, Description;
}

namespace ServerEnd
{
    class ServerCode
    {
        public static string SqlConLoc = "Data Source=(localdb)\\Projects;Initial Catalog=AprvSys_db;Integrated Security=True";


        static void Main(string[] args)
        {
            Console.WriteLine("Starting: Creating Socket Objeot");
            int port = 8080;//服务器端口号
            //string host = "192.168.1.103";//服务器IP地址
            string host = "10.3.0.1";//服务器IP地址

            Socket listener = new Socket(AddressFamily.InterNetwork,
                                        SocketType.Stream,
                                        ProtocolType.Tcp);//创建一个Socket类 

            listener.Bind(new IPEndPoint(IPAddress.Parse(host), port));//绑定8080端口 
            listener.Listen(10);//开始监听 

            while (true)
            {
                Console.WriteLine("Waiting for connection on port 8080");
                string recvStr = string.Empty;
                Socket socket = listener.Accept();//为新建连接创建新的Socket。 

                while (true)
                {
                    byte[] recvBytes = new byte[1024];
                    int numBytes = socket.Receive(recvBytes);
                    Console.WriteLine("Receiving ...");
                    recvStr += Encoding.UTF8.GetString(recvBytes, 0, numBytes);
                    if (recvStr.IndexOf("[FINAL]") > -1)
                    {
                        recvStr = recvStr.Replace("[FINAL]", "");
                        break;
                    }
                }

                //Console.WriteLine("Received string: {0}", recvStr);//接受到的数据打印出来
                //反序列化收到的字符串为JSON对象
                SocketRequest request = null;
                JavaScriptSerializer jss = new JavaScriptSerializer();
                request = jss.Deserialize(recvStr, typeof(SocketRequest)) as SocketRequest;
                Console.WriteLine(recvStr);
                var Option = request.Op;
                if (Option.Equals("Login"))
                {
                    Dictionary<string, object> UserData = (Dictionary<string, object>)request.RequestData;
                    string UserName = UserData["Username"].ToString();
                    string PassWord = UserData["Password"].ToString();
                    Console.WriteLine("Op: {0}; Username: {1}; Password: {2}", Option, UserName, PassWord);

                    //检查是否有该用户及其密码是否匹配
                    var dbcon = new SqlConnection(SqlConLoc);
                    var dbcommand = dbcon.CreateCommand();
                    dbcommand.CommandText = "SELECT Password, AprvCon, UpldCon, RealName, Company, Email, Description FROM UserInfoTable WHERE Username = '" + UserName + "'";
                    dbcon.Open();

                    SqlDataReader dbreader = dbcommand.ExecuteReader();
                    /*
                    while (dbreader.Read())
                    {
                        Console.WriteLine("User: {0}; Password: {1}",
                            dbreader[0].ToString(), dbreader[1].ToString());
                    }
                    */
                    if (dbreader.Read())
                    {
                        if (PassWord.Equals(dbreader[0].ToString()))
                        {
                            //验证成功 返回用户数据
                            //待审批列表
                            LoginResponse loginresp = new LoginResponse();

                            string aprvconlist = dbreader[1].ToString();
                            string upldconlist = dbreader[2].ToString();
                            string realname = dbreader[3].ToString();
                            string company = dbreader[4].ToString();
                            string email = dbreader[5].ToString();
                            string description = dbreader[6].ToString();
                            Console.WriteLine("{0}, {1}", aprvconlist, upldconlist);
                            dbreader.Close();
                            if (aprvconlist.Length > 0)
                            {
                                string[] ConList = aprvconlist.Split(new Char[] { ';' });
                                Contract[] AprvConListArray = new Contract[ConList.Length];
                                int sum = 0;
                                foreach (string con in ConList)
                                {
                                    dbcommand.CommandText = "SELECT * FROM ContractTable WHERE Id = " + con;
                                    var dbreader_con = dbcommand.ExecuteReader();
                                    while (dbreader_con.Read())
                                    {
                                        //Console.WriteLine("{0}, {1}, {2}", dbreader_con[1].ToString(), dbreader_con[2].ToString(), dbreader_con[3].ToString());                            
                                        var contract = new Contract();
                                        contract.id = int.Parse(dbreader_con[0].ToString());
                                        contract.title = dbreader_con[1].ToString();
                                        contract.author = dbreader_con[2].ToString();
                                        contract.status = int.Parse(dbreader_con[3].ToString());
                                        contract.sum = int.Parse(dbreader_con[4].ToString());
                                        contract.content = dbreader_con[5].ToString();
                                        contract.time = dbreader_con[6].ToString();
                                        contract.number = dbreader_con[7].ToString();
                                        contract.category = dbreader_con[8].ToString();
                                        contract.proname = dbreader_con[9].ToString();
                                        contract.pronum = dbreader_con[10].ToString();
                                        contract.goodname = dbreader_con[11].ToString();
                                        contract.goodnum = dbreader_con[12].ToString();
                                        contract.sum_cat = dbreader_con[13].ToString();
                                        contract.payment = dbreader_con[14].ToString();
                                        contract.rate = dbreader_con[15].ToString();
                                        contract.signname = dbreader_con[16].ToString();
                                        contract.dealname = dbreader_con[17].ToString();
                                        contract.period = dbreader_con[18].ToString();
                                        contract.site = dbreader_con[19].ToString();
                                        contract.punish = dbreader_con[20].ToString();
                                        contract.ps = dbreader_con[21].ToString();
                                        contract.comment = dbreader_con[22].ToString();

                                        AprvConListArray[sum] = contract;
                                        sum++;
                                    }
                                    dbreader_con.Close();
                                }
                                loginresp.AprvConList = AprvConListArray;
                            }
                            if (upldconlist.Length > 0)
                            {

                                //上传列表
                                string[] ConList_upld = upldconlist.Split(new Char[] { ';' });
                                Contract[] UpldConListArray = new Contract[ConList_upld.Length];
                                int sum = 0;
                                foreach (string con in ConList_upld)
                                {
                                    dbcommand.CommandText = "SELECT * FROM ContractTable WHERE Id =" + con;
                                    //Console.WriteLine(dbcommand.CommandText);
                                    var dbreader_con_upld = dbcommand.ExecuteReader();
                                    while (dbreader_con_upld.Read())
                                    {
                                        //Console.WriteLine("{0}, {1}, {2}", dbreader_con_upld[1].ToString(), dbreader_con_upld[2].ToString(), dbreader_con_upld[3].ToString());                            
                                        var contract = new Contract();
                                        contract.id = int.Parse(dbreader_con_upld[0].ToString());
                                        contract.title = dbreader_con_upld[1].ToString();
                                        contract.author = dbreader_con_upld[2].ToString();
                                        contract.status = int.Parse(dbreader_con_upld[3].ToString());
                                        contract.sum = int.Parse(dbreader_con_upld[4].ToString());
                                        contract.content = dbreader_con_upld[5].ToString();
                                        contract.time = dbreader_con_upld[6].ToString();
                                        contract.number = dbreader_con_upld[7].ToString();
                                        contract.category = dbreader_con_upld[8].ToString();
                                        contract.proname = dbreader_con_upld[9].ToString();
                                        contract.pronum = dbreader_con_upld[10].ToString();
                                        contract.goodname = dbreader_con_upld[11].ToString();
                                        contract.goodnum = dbreader_con_upld[12].ToString();
                                        contract.sum_cat = dbreader_con_upld[13].ToString();
                                        contract.payment = dbreader_con_upld[14].ToString();
                                        contract.rate = dbreader_con_upld[15].ToString();
                                        contract.signname = dbreader_con_upld[16].ToString();
                                        contract.dealname = dbreader_con_upld[17].ToString();
                                        contract.period = dbreader_con_upld[18].ToString();
                                        contract.site = dbreader_con_upld[19].ToString();
                                        contract.punish = dbreader_con_upld[20].ToString();
                                        contract.ps = dbreader_con_upld[21].ToString();
                                        contract.comment = dbreader_con_upld[22].ToString();

                                        UpldConListArray[sum] = contract;
                                        sum++;
                                    }
                                    dbreader_con_upld.Close();
                                }
                                loginresp.UpldConList = UpldConListArray;
                            }

                            //个人信息
                            loginresp.RealName = realname;
                            loginresp.Company = company;
                            loginresp.Description = description;
                            loginresp.Email = email;

                            //给客户端回复数据
                            var response = new SocketResponse
                            {
                                Op = "Correct",
                                ResultData = (object)loginresp
                            };

                            string replyString = jss.Serialize(response) + "\n";
                            byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                            Console.WriteLine(replyString);
                            socket.Send(replyMessage);
                        }
                        else
                        {
                            var response = new SocketResponse
                            {
                                Op = "Wrong",
                                ResultData = null
                            };
                            string replyString = jss.Serialize(response) + "\n";
                            byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                            socket.Send(replyMessage);
                        }
                    }
                    else
                    {
                        var response = new SocketResponse
                        {
                            Op = "Wrong",
                            ResultData = null
                        };
                        string replyString = jss.Serialize(response) + "\n";
                        byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                        socket.Send(replyMessage);
                    }
                    dbcon.Close();
                }
                else if (Option.Equals("UpdateAprvResult"))
                {
                    //用审批结果更新contract表
                    Dictionary<string, object> UserData = (Dictionary<string, object>)request.RequestData;
                    string Comment = UserData["Comment"].ToString();
                    string Result = UserData["Result"].ToString();
                    string ContractId = UserData["ContractId"].ToString();
                    Console.WriteLine("Op: {0}; Result: {1}; Comment: {2}", Option, Result, Comment);

                    var dbcon = new SqlConnection(SqlConLoc);
                    var dbcommand = dbcon.CreateCommand();
                    dbcommand.CommandText = "UPDATE ContractTable SET Status = " + Result + ", Comment = '" + Comment + "' WHERE Id = " + ContractId;
                    dbcon.Open();
                    int count = dbcommand.ExecuteNonQuery();
                    if (count > 0)
                    {
                        var response = new SocketResponse
                        {
                            Op = "Correct",
                            ResultData = null
                        };
                        string replyString = jss.Serialize(response) + "\n";
                        byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                        socket.Send(replyMessage);
                    }
                    else
                    {
                        var response = new SocketResponse
                        {
                            Op = "Wrong",
                            ResultData = null
                        };
                        string replyString = jss.Serialize(response) + "\n";
                        byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                        socket.Send(replyMessage);
                    }
                    dbcon.Close();
                }
                else if (Option.Equals("Upload"))
                {
                    Dictionary<string, object> UserData = (Dictionary<string, object>)request.RequestData;
                    string SignName = UserData["signname"].ToString();
                    string Author = UserData["author"].ToString();

                    Console.WriteLine("Op: {0}; Author: {1}; SignName: {2}", Option, Author, SignName);

                    //检查是否待审批用户是否存在
                    var dbcon = new SqlConnection(SqlConLoc);
                    var dbcommand = dbcon.CreateCommand();
                    dbcommand.CommandText = "SELECT AprvCon FROM UserInfoTable WHERE Username = '" + SignName + "'";
                    dbcon.Open();

                    SqlDataReader dbreader = dbcommand.ExecuteReader();

                    if (dbreader.Read())
                    {
                        string aprvconlist = dbreader[0].ToString();
                        //Console.WriteLine("{0}, {1}", aprvconlist, upldconlist);    
                        dbreader.Close();

                        //验证成功 新建合同
                        dbcommand.CommandText = "SELECT Id FROM ContractTable";
                        var dbreader_id = dbcommand.ExecuteReader();
                        int NewId = 0;
                        while (dbreader_id.Read()) NewId++;
                        dbreader_id.Close();
                        string sql = "INSERT INTO ContractTable VALUES(" + NewId.ToString() + ", ";
                        sql += "'" + UserData["title"].ToString() + "', ";
                        sql += "'" + Author + "', ";
                        sql += "0, ";
                        sql += UserData["sum"].ToString() + ", ";
                        sql += "'" + UserData["content"].ToString() + "', ";
                        sql += "'" + UserData["date"].ToString() + "', ";
                        sql += "'" + UserData["connum"].ToString() + "', ";
                        sql += "'" + UserData["cat"].ToString() + "', ";
                        sql += "'" + UserData["proname"].ToString() + "', ";
                        sql += "'" + UserData["pronum"].ToString() + "', ";
                        sql += "'" + UserData["goodname"].ToString() + "', ";
                        sql += "'" + UserData["goodnum"].ToString() + "', ";
                        sql += "'" + UserData["sumcat"].ToString() + "', ";
                        sql += "'" + UserData["payment"].ToString() + "', ";
                        sql += "'" + UserData["rate"].ToString() + "', ";
                        sql += "'" + SignName + "', ";
                        sql += "'" + Author + "', ";
                        sql += "'" + UserData["period"].ToString() + "', ";
                        sql += "'" + UserData["site"].ToString() + "', ";
                        sql += "'" + UserData["punish"].ToString() + "', ";
                        sql += "'" + UserData["ps"].ToString() + "', ";
                        sql += "'')";
                        dbcommand.CommandText = sql;
                        int flag = dbcommand.ExecuteNonQuery();
                        if (flag > 0)
                        {
                            //更新待审批人信息
                            aprvconlist += ";" + NewId.ToString();
                            dbcommand.CommandText = "UPDATE UserInfoTable SET AprvCon = '" + aprvconlist + "' WHERE Username = '" + SignName + "'";
                            dbcommand.ExecuteNonQuery();

                            //更新上传者信息
                            dbcommand.CommandText = "SELECT UpldCon FROM UserInfoTable WHERE Username = '" + Author + "'";
                            SqlDataReader dbreader_au = dbcommand.ExecuteReader();
                            dbreader_au.Read();
                            string upldconlist = dbreader_au[0].ToString();
                            dbreader_au.Close();
                            upldconlist += ";" + NewId.ToString();
                            dbcommand.CommandText = "UPDATE UserInfoTable SET UpldCon = '" + upldconlist + "' WHERE Username = '" + Author + "'";
                            dbcommand.ExecuteNonQuery();

                            //给客户端回复数据
                            var response = new SocketResponse
                            {
                                Op = "Correct",
                                ResultData = null
                            };
                            string replyString = jss.Serialize(response) + "\n";
                            byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                            Console.WriteLine(replyString);
                            socket.Send(replyMessage);
                        }
                    }
                    else
                    {
                        //SignName有问题
                        var response = new SocketResponse
                        {
                            Op = "Wrong",
                            ResultData = null
                        };
                        string replyString = jss.Serialize(response) + "\n";
                        byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                        socket.Send(replyMessage);
                    }
                    dbcon.Close();
                }
                else if (Option.Equals("NewUser"))
                {
                    Dictionary<string, object> UserData = (Dictionary<string, object>)request.RequestData;
                    string UserName = UserData["username"].ToString();

                    Console.WriteLine("Op: {0}; UserName: {1};", Option, UserName);

                    //检查用户名是否存在
                    var dbcon = new SqlConnection(SqlConLoc);
                    var dbcommand = dbcon.CreateCommand();
                    dbcommand.CommandText = "SELECT Id FROM UserInfoTable WHERE Username = '" + UserName + "'";
                    dbcon.Open();

                    SqlDataReader dbreader = dbcommand.ExecuteReader();

                    if (dbreader.Read())
                    {
                        dbreader.Close();
                        //Username已被注册
                        var response = new SocketResponse
                        {
                            Op = "Wrong",
                            ResultData = null
                        };
                        string replyString = jss.Serialize(response) + "\n";
                        byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                        socket.Send(replyMessage);
                    }
                    else
                    {
                        dbreader.Close();

                        //验证成功 新建用户
                        dbcommand.CommandText = "SELECT Id FROM UserInfoTable";
                        var dbreader_uid = dbcommand.ExecuteReader();
                        int NewId = 0;
                        while (dbreader_uid.Read()) NewId++;
                        dbreader_uid.Close();
                        string sql = "INSERT INTO UserInfoTable VALUES(" + NewId.ToString() + ", ";
                        sql += "'" + UserName + "', ";
                        sql += "'" + UserData["password"].ToString() + "', ";
                        sql += "'0', ";
                        sql += "'0', ";
                        sql += "'" + UserData["realname"].ToString() + "', ";
                        sql += "'" + UserData["company"].ToString() + "', ";
                        sql += "'" + UserData["email"].ToString() + "', ";
                        sql += "'" + UserData["description"].ToString() + "')";
                        dbcommand.CommandText = sql;
                        int flag = dbcommand.ExecuteNonQuery();
                        if (flag > 0)
                        {
                            //给客户端回复数据
                            var response = new SocketResponse
                            {
                                Op = "Correct",
                                ResultData = null
                            };
                            string replyString = jss.Serialize(response) + "\n";
                            byte[] replyMessage = Encoding.UTF8.GetBytes(replyString);
                            Console.WriteLine(replyString);
                            socket.Send(replyMessage);
                        }
                    }
                    dbcon.Close();
                }
            }
            listener.Close();            
        }
    }
}
