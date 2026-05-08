package com.example.naming.command;

import com.example.naming.config.BlacklistConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class UpdateBlacklistCommand implements CommandLineRunner {

    @Autowired
    private BlacklistConfig blacklistConfig;

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && args[0].equals("--update-blacklist")) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("当前黑名单字符数量: " + blacklistConfig.getBadChars().size());
            System.out.print("请输入新的黑名单字符（用逗号分隔）: ");
            String input = scanner.nextLine();
            blacklistConfig.setChars(input);
            System.out.println("黑名单已更新！");
            System.out.println("新的黑名单字符数量: " + blacklistConfig.getBadChars().size());
            scanner.close();
        }
    }
}