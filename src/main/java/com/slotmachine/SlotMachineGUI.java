package com.slotmachine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class SlotMachineGUI extends JFrame {
    private static final Color PIXEL_BG = new Color(40, 44, 52);
    private static final Color PIXEL_PANEL = new Color(60, 64, 72);
    private static final Color PIXEL_GOLD = new Color(255, 215, 0);
    private static final Color PIXEL_GREEN = new Color(98, 209, 150);
    private static final Color PIXEL_RED = new Color(240, 113, 120);
    private static final Font PIXEL_FONT = new Font("Monospaced", Font.BOLD, 16);
    private static final Font PIXEL_FONT_BIG = new Font("Monospaced", Font.BOLD, 24);
    private static final Font PIXEL_FONT_HUGE = new Font("Monospaced", Font.BOLD, 24);

    private GameEngine gameEngine;
    private JLabel[] reelLabels;
    private JLabel saldoLabel;
    private JLabel apostaLabel;
    private JLabel mensagemLabel;
    private JButton spinButton;
    private JButton aumentarButton;
    private JButton diminuirButton;
    private Timer animationTimer;
    private int apostaAtual = 10;
    private boolean girando = false;
    private Map<String, ImageIcon> symbolImages;

    public SlotMachineGUI() {
        gameEngine = new GameEngine(100);
        loadImages();
        initializeUI();

        // Spin inicial para mostrar símbolos
        String[] simbolosIniciais = gameEngine.spin();
        for (int i = 0; i < reelLabels.length; i++) {
            setReelSymbol(reelLabels[i], simbolosIniciais[i]);
        }
    }

    private void initializeUI() {
        setTitle("🎰 SLOT MACHINE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(PIXEL_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(PIXEL_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createReelPanel(), BorderLayout.CENTER);
        mainPanel.add(createControlPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);

        updateDisplay();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(PIXEL_BG);

        JLabel titleLabel = new JLabel("🎰 SLOT MACHINE 🎰");
        titleLabel.setForeground(PIXEL_GOLD);
        titleLabel.setFont(PIXEL_FONT_BIG);

        panel.add(titleLabel);
        return panel;
    }

    private JPanel createReelPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PIXEL_BG);

        JPanel reelsContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        reelsContainer.setBackground(PIXEL_BG);
        reelsContainer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        reelLabels = new JLabel[3];
        for (int i = 0; i < 3; i++) {
            reelLabels[i] = new JLabel("", SwingConstants.CENTER);
            reelLabels[i].setFont(PIXEL_FONT_HUGE);
            reelLabels[i].setOpaque(true);
            reelLabels[i].setBackground(PIXEL_PANEL);
            reelLabels[i].setForeground(Color.WHITE);
            reelLabels[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PIXEL_GOLD, 3),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            reelLabels[i].setPreferredSize(new Dimension(100, 80));
            reelsContainer.add(reelLabels[i]);
        }

        panel.add(reelsContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PIXEL_BG);

        JPanel infoPanel = createInfoPanel();
        JPanel buttonPanel = createButtonPanel();

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBackground(PIXEL_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        saldoLabel = new JLabel("SALDO: R$ 100", SwingConstants.CENTER);
        saldoLabel.setForeground(PIXEL_GREEN);
        saldoLabel.setFont(PIXEL_FONT);

        apostaLabel = new JLabel("APOSTA: R$ 10", SwingConstants.CENTER);
        apostaLabel.setForeground(Color.WHITE);
        apostaLabel.setFont(PIXEL_FONT);

        mensagemLabel = new JLabel("BOA SORTE!", SwingConstants.CENTER);
        mensagemLabel.setForeground(PIXEL_GOLD);
        mensagemLabel.setFont(PIXEL_FONT);

        panel.add(saldoLabel);
        panel.add(apostaLabel);
        panel.add(mensagemLabel);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(PIXEL_BG);

        diminuirButton = createPixelButton("- APOSTA", PIXEL_RED);
        diminuirButton.addActionListener(e -> alterarAposta(-5));

        spinButton = createPixelButton("🎰 GIRAR 🎰", PIXEL_GREEN);
        spinButton.addActionListener(this::spin);

        aumentarButton = createPixelButton("+ APOSTA", PIXEL_GREEN);
        aumentarButton.addActionListener(e -> alterarAposta(5));

        panel.add(diminuirButton);
        panel.add(spinButton);
        panel.add(aumentarButton);

        return panel;
    }

    private JButton createPixelButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(PIXEL_FONT);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        return button;
    }

    private void loadImages() {
        symbolImages = new HashMap<>();

        // Mapeamento dos símbolos para os arquivos
        Map<String, String> symbolFiles = Map.of(
                "uva", "/images/1f347.png",
                "melancia", "/images/1f349.png",
                "limao", "/images/1f34b.png",
                "laranja", "/images/1f34a.png",
                "banana", "/images/1f34c.png",
                "cereja", "/images/1f352.png"
        );

        for (Map.Entry<String, String> entry : symbolFiles.entrySet()) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(entry.getValue()));
                // Redimensionar para 48x48
                Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                symbolImages.put(entry.getKey(), new ImageIcon(img));
            } catch (Exception e) {
                System.err.println("Erro ao carregar imagem: " + entry.getValue());
                // Fallback para texto se imagem falhar
                symbolImages.put(entry.getKey(), null);
            }
        }
    }

    private void setReelSymbol(JLabel reel, String symbol) {
        ImageIcon icon = symbolImages.get(symbol);
        if (icon != null) {
            reel.setIcon(icon);
            reel.setText("");
        } else {
            reel.setIcon(null);
            reel.setText(symbol.toUpperCase());
        }
    }

    private void alterarAposta(int delta) {
        int novaAposta = apostaAtual + delta;
        if (novaAposta >= 5 && gameEngine.podeApostar(novaAposta)) {
            apostaAtual = novaAposta;
            updateDisplay();
        }
    }

    private void spin(ActionEvent e) {
        if (girando || !gameEngine.podeApostar(apostaAtual)) return;

        girando = true;
        spinButton.setEnabled(false);
        aumentarButton.setEnabled(false);
        diminuirButton.setEnabled(false);

        mensagemLabel.setText("GIRANDO...");
        mensagemLabel.setForeground(Color.YELLOW);

        String[] simbolosAnimacao = {"uva", "melancia", "limao", "laranja", "banana", "cereja"};
        int[] frameCount = {0};

        animationTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JLabel reel : reelLabels) {
                    String randomSymbol = simbolosAnimacao[(int)(Math.random() * simbolosAnimacao.length)];
                    setReelSymbol(reel, randomSymbol);
                }

                frameCount[0]++;
                if (frameCount[0] >= 20) {
                    animationTimer.stop();
                    finalizarSpin();
                }
            }
        });

        animationTimer.start();
    }

    private void finalizarSpin() {
        String[] resultado = gameEngine.spin();

        for (int i = 0; i < reelLabels.length; i++) {
            setReelSymbol(reelLabels[i], resultado[i]);
        }

        int premio = gameEngine.processarAposta(resultado, apostaAtual);

        if (premio > 0) {
            mensagemLabel.setText(String.format("GANHOU R$ %d! (x%d)", premio, premio / apostaAtual));
            mensagemLabel.setForeground(PIXEL_GREEN);
        } else {
            mensagemLabel.setText("TENTE NOVAMENTE!");
            mensagemLabel.setForeground(PIXEL_RED);
        }

        updateDisplay();

        girando = false;
        spinButton.setEnabled(gameEngine.temSaldo());
        aumentarButton.setEnabled(true);
        diminuirButton.setEnabled(true);

        if (!gameEngine.temSaldo()) {
            mensagemLabel.setText("GAME OVER!");
            mensagemLabel.setForeground(PIXEL_RED);
            spinButton.setText("GAME OVER");
        }
    }

    private void updateDisplay() {
        saldoLabel.setText("SALDO: R$ " + gameEngine.getSaldo());
        apostaLabel.setText("APOSTA: R$ " + apostaAtual);

        if (gameEngine.getSaldo() < apostaAtual) {
            apostaAtual = Math.max(5, gameEngine.getSaldo());
        }
    }
}