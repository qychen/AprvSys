using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Net.Sockets;
//using System.Threading;

namespace ClientEnd
{
    class ClientCode
    {
        static void Main(string[] args)
        {
            byte[] receiveBytes = new byte[1024];
            int port =8080;//服务器端口
            string host = "10.3.0.1";  //服务器ip
            
            IPAddress ip = IPAddress.Parse(host);
            IPEndPoint ipe = new IPEndPoint(ip, port);//把ip和端口转化为IPEndPoint实例 
            Console.WriteLine("Starting Creating Socket Object");
            Socket sender = new Socket(AddressFamily.InterNetwork, 
                                        SocketType.Stream, 
                                        ProtocolType.Tcp);//创建一个Socket 
            sender.Connect(ipe);//连接到服务器 
            string sendingMessage = "Hello World!";
            byte[] forwardingMessage = Encoding.ASCII.GetBytes(sendingMessage + "[FINAL]");
            sender.Send(forwardingMessage);
            int totalBytesReceived = sender.Receive(receiveBytes);
            Console.WriteLine("Message provided from server: {0}",
                              Encoding.ASCII.GetString(receiveBytes,0,totalBytesReceived));
            //byte[] bs = Encoding.ASCII.GetBytes(sendStr);

            sender.Shutdown(SocketShutdown.Both);  
            sender.Close();
            Console.ReadLine();
        }
    }
}
