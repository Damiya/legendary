package com.gvaneyck.rtmp;

import java.net.InetSocketAddress;

/**
 * Class for storing connection information
 */
public class ServerInfo {
    public static final ServerInfo NA = new ServerInfo("NA", "NA1", "North America", "prod.na1.lol.riotgames.com", "https://lq.na1.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo EUW = new ServerInfo("EUW", "EUW1", "Europe West", "prod.eu.lol.riotgames.com", "https://lq.eu.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo EUNE = new ServerInfo("EUNE", "EUN1", "Europe Nordic & East", "prod.eun1.lol.riotgames.com", "https://lq.eun1.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo KR = new ServerInfo("KR", "KR", "Korea", "prod.kr.lol.riotgames.com", "https://lq.kr.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo BR = new ServerInfo("BR", "BR1", "Brazil", "prod.br.lol.riotgames.com", "https://lq.br.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo TR = new ServerInfo("TR", "TR1", "Turkey", "prod.tr.lol.riotgames.com", "https://lq.tr.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo RU = new ServerInfo("RU", "RU", "Russia", "prod.ru.lol.riotgames.com", "https://lq.ru.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo LAN = new ServerInfo("LAN", "LA1", "Latin America North", "prod.la1.lol.riotgames.com", "https://lq.la1.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo LAS = new ServerInfo("LAS", "LA2", "Latin America South", "prod.la2.lol.riotgames.com", "https://lq.la2.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo OCE = new ServerInfo("OCE", "OC1", "Oceania", "prod.oc1.lol.riotgames.com", "https://lq.oc1.lol.riotgames.com/login-queue/rest/queues/lol/");
    public static final ServerInfo PBE = new ServerInfo("PBE", "PBE1", "Public Beta Environment", "prod.pbe1.lol.riotgames.com", "https://lq.pbe1.lol.riotgames.com/");
    public static final ServerInfo SG = new ServerInfo("SG", "SG", "Singapore/Malaysia", "prod.lol.garenanow.com", "https://lq.lol.garenanow.com/login-queue/rest/queues/lol/", true);
    public static final ServerInfo TW = new ServerInfo("TW", "TW", "Taiwan", "prodtw.lol.garenanow.com", "https://loginqueuetw.lol.garenanow.com/login-queue/rest/queues/lol/", true);
    public static final ServerInfo TH = new ServerInfo("TH", "TH", "Thailand", "prodth.lol.garenanow.com", "https://lqth.lol.garenanow.com/login-queue/rest/queues/lol/", true);
    public static final ServerInfo PH = new ServerInfo("PH", "PH", "Phillipines", "prodph.lol.garenanow.com", "https://lqph.lol.garenanow.com/login-queue/rest/queues/lol/", true);
    public static final ServerInfo VN = new ServerInfo("VN", "VN", "Vietnam", "prodvn.lol.garenanow.com", "https://lqvn.lol.garenanow.com/login-queue/rest/queues/lol/", true);

    public String region;
    public String platform; // The internal platform name, used for spectating
    public String name;
    public String hostName;
    public String loginQueue;
    public boolean useGarena;

    public ServerInfo(String region, String platform, String name, String hostName, String loginQueue) {
        this(region, platform, name, hostName, loginQueue, false);
    }

    public ServerInfo(String region, String platform, String name, String hostName, String loginQueue, boolean useGarena) {
        this.region = region;
        this.platform = platform;
        this.name = name;
        this.hostName = hostName;
        this.loginQueue = loginQueue;
        this.useGarena = useGarena;
    }

    public String toString() {
        return name;
    }

    public int getSecurePort() {
        return 2099;
    }

    public int getInsecurePort() {
        return 2043;
    }
}
