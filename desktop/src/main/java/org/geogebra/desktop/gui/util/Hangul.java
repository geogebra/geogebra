package org.geogebra.desktop.gui.util;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Hangul extends Frame {

	private static final long serialVersionUID = 1L;

	public static void main(String args[]) {
		Hangul frame = null;
		frame = new Hangul();
		frame.init();
	}

	Hangul() {
		setLayout(new BorderLayout());
		setBackground(Color.gray);
		setTitle("\uD55C\uAE00 \uC624\uD1A0\uB9C8\uD0C0 - \uB2E4\uBE44\uB3C4\uD504\uD321");
		/********* \uD0C0\uC774\uD2C0 \uBA85 -> \uD559\uBC88 & \uC774\uB984 ***********/
		setVisible(true);
		addWindowListener(new ExitListener());
	}

	TextField cho_ = new TextField("\uCD08");
	TextField jung_ = new TextField("\uC911");
	TextField jong_ = new TextField("\uC885");
	TextField record_ = new TextField("", 20);

	Button[] menuButtons_ = null;
	Button delBtn_ = new Button("\uC9C0\uC6B0\uAE30");

	void init() {
		Panel upperPanel = new Panel();
		Panel recordPanel = new Panel();
		Panel lowerPanel = new Panel();
		add("North", upperPanel);
		add("Center", recordPanel);
		add("South", lowerPanel);
		upperPanel.setVisible(true);
		recordPanel.setVisible(true);
		lowerPanel.setVisible(true);
		upperPanel.setLayout(new FlowLayout());
		upperPanel.add(cho_);
		upperPanel.add(jung_);
		upperPanel.add(jong_); // <- \uC885\uC131 \uD328\uB110 \uCD94\uAC00
		recordPanel.setLayout(new FlowLayout());
		recordPanel.add(record_);
		recordPanel.add(delBtn_);
		initButtons(lowerPanel);
		pack();
		setBackground(Color.lightGray);
	}

	/*************************************** \uBC84\uD2BC \uCD08\uAE30\uD654 / \uBC84\uD2BC\uBA85 \uC0DD\uC131 START *************************************/
	void initButtons(Panel p) {
		p.setLayout(new GridLayout(4, 2));
		menuButtons_ = new Button[8];
		MyActionListener al = new MyActionListener();
		menuButtons_[0] = new Button("\u3131");
		menuButtons_[0].addActionListener(al);
		p.add(menuButtons_[0]);
		menuButtons_[0].setVisible(true);

		menuButtons_[1] = new Button("\u3134");
		menuButtons_[1].addActionListener(al);
		p.add(menuButtons_[1]);
		menuButtons_[1].setVisible(true);

		menuButtons_[2] = new Button("-");
		menuButtons_[2].addActionListener(al);
		p.add(menuButtons_[2]);
		menuButtons_[2].setVisible(true);

		menuButtons_[3] = new Button("\u3145");
		menuButtons_[3].addActionListener(al);
		p.add(menuButtons_[3]);
		menuButtons_[3].setVisible(true);

		menuButtons_[4] = new Button("\u314F");
		menuButtons_[4].addActionListener(al);
		p.add(menuButtons_[4]);
		menuButtons_[4].setVisible(true);

		menuButtons_[5] = new Button("\u3153");
		menuButtons_[5].addActionListener(al);
		p.add(menuButtons_[5]);
		menuButtons_[5].setVisible(true);

		menuButtons_[6] = new Button("\u3161");
		menuButtons_[6].addActionListener(al);
		p.add(menuButtons_[6]);
		menuButtons_[6].setVisible(true);

		menuButtons_[7] = new Button("\u3163");
		menuButtons_[7].addActionListener(al);
		p.add(menuButtons_[7]);
		menuButtons_[7].setVisible(true);

		delBtn_.addActionListener(al);
	}

	/*************************************** \uBC84\uD2BC \uCD08\uAE30\uD654 / \uBC84\uD2BC\uBA85 \uC0DD\uC131 END *************************************/

	/************************************************* \uCD08\uC131 \uAD00\uB828 ******************************************************/
	/****************************** \uCD08\uC131 \uC0C1\uD0DC\uC804\uC774\uB3C4 ***************************************************/
	int[][] choTransTable = { { 1, 2, 3, 4 }, // 0
			{ 5, 7, 6, -1 }, // 1 \u3131
			{ -1, -1, -1, -1 }, // 2 \u3134
			{ 8, 10, 9, 11 }, // 3
			{ -1, -1, -1, 12 }, // 4 \u3145
			{ -1, -1, -1, -1 }, // 5 \u3132
			{ -1, 13, -1, -1 }, // 6
			{ -1, -1, 14, -1 }, // 7 \u3141
			{ -1, -1, -1, -1 }, // 8 \u314B
			{ 15, 16, -1, 17 }, // 9
			{ -1, 18, -1, -1 }, // 10 \u3137
			{ -1, -1, -1, -1 }, // 11 \u3148
			{ -1, -1, -1, 19 }, // 12 \u3147
			{ -1, -1, -1, -1 }, // 13 \u3139
			{ -1, -1, 20, -1 }, // 14 \u3142
			{ -1, 21, -1, -1 }, // 15
			{ -1, -1, -1, -1 }, // 16 \u314C
			{ -1, -1, -1, 22 }, // 17 \u314A
			{ -1, -1, -1, -1 }, // 18 \u3138
			{ -1, -1, -1, -1 }, // 19 \u3146
			{ -1, -1, -1, -1 }, // 20 \u3143
			{ -1, -1, -1, -1 }, // 21 \u314D
			{ -1, -1, -1, -1 }, // 22 \u314E
	};
	/****************************** \uCD08\uC131 \uC0C1\uD0DC\uC804\uC774\uB3C4 ***************************************************/

	String choSungs_ = " \u3131\u3134 \u3145\u3132 \u3141\u314B \u3137\u3148\u3147\u3139\u3142 \u314C\u314A\u3138\u3146\u3143\u314D\u314E";
	/*** \uCD08\uC131 \uC0C1\uD0DC\uBCC4 \uAC12 ***/
	// 01 2 34 5 67 8 91 1 1 1 1 11 1 1 1 2 2 2
	// 0 1 2 3 4 56 7 8 9 0 1 2
	int choState_ = 0; // \uCD08\uC131 \uCD08\uAE30\uC0C1\uD0DC

	/************************* \uCD08\uC131 \uC0C1\uD0DC \uBCC0\uD654\uD568\uC218 START ***********************/
	// \uC218\uC815\uD544\uC694
	void moveCho(int num) {
		if (num >= 5 && num <= 8)
			return; // \uB9CC\uC57D \uBAA8\uC74C\uC774
					// \uB4E4\uC5B4\uC654\uB2E4\uBA74
					// \uB418\uB3CC\uC544\uAC04\uB2E4
		if (0 <= choState_ && choState_ <= 22) {
			choState_ = choTransTable[choState_][num];
			state_ = 1;
			if (choState_ < 0) { // choState_\uAC00 0\uBCF4\uB2E4
									// \uC791\uB2E4\uBA74 \uC5D0\uB7EC ->
									// \uB9AC\uC14B,
									// \uC5D0\uB7EC\uBA54\uC138\uC9C0\uB97C
									// \uBCF4\uB0B8\uB2E4.
				reset();
				setError();
			} else
				cho_.setText("" + choSungs_.charAt(choState_));
		} else {
			reset();
			setError();
		}
	}

	/************************* \uCD08\uC131 \uC0C1\uD0DC \uBCC0\uD654\uD568\uC218 END ***********************/
	/***********************************************************************************************************/

	/****************************** \uC911\uC131 \uC0C1\uD0DC\uC804\uC774\uB3C4 START ***************************************************/
	int[][] jungTransTable = { { 1, 2, 4, 3 }, // 0
			{ -1, 5, 7, 6 }, // 1 \u314F
			{ -1, -1, -1, 8 }, // 2 \u3153
			{ -1, -1, 10, 9 }, // 3 \u3163
			{ -1, 11, -1, 12 }, // 4 \u3161
			{ -1, -1, -1, -1 }, // 5 \u3152
			{ -1, -1, -1, -1 }, // 6 \u3150
			{ -1, -1, -1, -1 }, // 7 \u3151
			{ -1, -1, -1, -1 }, // 8 \u3154
			{ -1, -1, 13, -1 }, // 9 \u3162
			{ 15, -1, -1, 14 }, // 10 \u3157
			{ -1, -1, -1, 16 }, // 11 \u3155
			{ -1, 18, -1, 17 }, // 12 \u315C
			{ -1, -1, -1, -1 }, // 13 \u315B
			{ -1, -1, -1, -1 }, // 14 \u315A
			{ -1, -1, -1, 19 }, // 15 \u3158
			{ -1, -1, -1, -1 }, // 16 \u3156
			{ -1, -1, -1, 20 }, // 17 \u3160
			{ -1, -1, -1, 21 }, // 18 \u315D
			{ -1, -1, -1, -1 }, // 19 \u3159
			{ -1, -1, -1, -1 }, // 20 \u315F
			{ -1, -1, -1, -1 }, // 21 \u315E
	};
	/****************************** \uC911\uC131 \uC0C1\uD0DC\uC804\uC774\uB3C4 END ***************************************************/

	String jungSungs_ = " \uC544\uC5B4\uC774\uC73C\uC598\uC560\uC57C\uC5D0\uC758\uC624\uC5EC\uC6B0\uC694\uC678\uC640\uC608\uC720\uC6CC\uC65C\uC704\uC6E8";
	/*** \uC911\uC131 \uC0C1\uD0DC\uBCC4 \uAC12 ***/
	// 01 2 3 4 5 6 7 8 9 1 1 1 1 1 1 1 1 1 1 2 2
	// 0 1 2 3 4 5 6 7 8 9 0 1
	int jungState_ = 0; // \uC911\uC131 \uCD08\uAE30\uC0C1\uD0DC

	/************************* \uC911\uC131 \uC0C1\uD0DC \uBCC0\uD654\uD568\uC218 START ***********************/
	// \uC218\uC815\uD544\uC694
	void moveJung(int num) {
		if (num >= 4 && num <= 7) {
			num = num - 4;
			jungState_ = jungTransTable[jungState_][num];
			state_ = 2;
			if (jungState_ == -1) {
				reset();
				setError();
			} else
				jung_.setText("" + jungSungs_.charAt(jungState_));
		}
	}

	/************************* \uC911\uC131 \uC0C1\uD0DC \uBCC0\uD654\uD568\uC218 END ***********************/

	/************************************************* \uC885\uC131 \uAD00\uB828 **********************************/
	/****************************** \uCD08\uC131 \uC0C1\uD0DC\uC804\uC774\uB3C4 ***************************************************/
	int[][] jongTransTable = { { 1, 2, 3, 4 }, // 0
			{ 5, 7, 6, 23 }, // 1 \u3131
			{ -1, -1, 24, 25 }, // 2 \u3134
			{ 8, 10, 9, 11 }, // 3
			{ -1, -1, -1, 12 }, // 4 \u3145
			{ -1, -1, -1, -1 }, // 5 \u3132
			{ -1, 13, -1, -1 }, // 6
			{ -1, -1, 14, -1 }, // 7 \u3141
			{ -1, -1, -1, -1 }, // 8 \u314B
			{ 15, 16, -1, 17 }, // 9
			{ -1, 18, -1, -1 }, // 10 \u3137
			{ -1, -1, -1, -1 }, // 11 \u3148
			{ -1, -1, -1, 19 }, // 12 \u3147
			{ 26, -1, 30, 29 }, // 13 \u3139
			{ -1, -1, 20, 33 }, // 14 \u3142
			{ -1, 21, -1, -1 }, // 15
			{ -1, -1, -1, -1 }, // 16 \u314C
			{ -1, -1, -1, 22 }, // 17 \u314A
			{ -1, -1, -1, -1 }, // 18 \u3138
			{ -1, -1, -1, -1 }, // 19 \u3146
			{ -1, -1, -1, -1 }, // 20 \u3143
			{ -1, -1, -1, -1 }, // 21 \u314D
			{ -1, -1, -1, -1 }, // 22 \u314E
			{ -1, -1, -1, -1 }, // 23 \u3133
			{ -1, -1, -1, -1 }, // 24 \u3135
			{ -1, -1, -1, -1 }, // 25 \u3136
			{ -1, 27, -1, -1 }, // 26 \u313A
			{ -1, -1, 28, -1 }, // 27 \u313B
			{ -1, -1, -1, -1 }, // 28 \u313C
			{ -1, -1, -1, -1 }, // 29 \u313D
			{ -1, 31, 32, 33 }, // 30
			{ -1, -1, -1, -1 }, // 31 \u313E
			{ -1, -1, -1, -1 }, // 32 \u313F
			{ -1, -1, -1, -1 }, // 33 \u3140
			{ -1, -1, -1, -1 }, // 34 \u3144
	};
	/****************************** \uC885\uC131 \uC0C1\uD0DC\uC804\uC774\uB3C4 ***************************************************/

	String jongSungs_ = " \u3131\u3134 \u3145\u3132 \u3141\u314B \u3137\u3148\u3147\u3139\u3142 \u314C\u314A\u3138\u3146\u3143\u314D\u314E\u3133\u3135\u3136\u313A\u313B\u313C\u313D \u313E\u313F\u3140\u3144";
	/*** \uC885\uC131 \uC0C1\uD0DC\uBCC4 \uAC12 ***/
	// 01 2 34 5 67 8 91 1 1 1 1 11 1 1 1 2 2 2 2 2 2 2 2 2 2 33 3 3
	// 0 1 2 3 4 56 7 8 9 0 1 2 3 4 5 6 7 8 9 01 2 3
	int jongState_ = 0; // \uC885\uC131 \uCD08\uAE30\uC0C1\uD0DC

	/************************* \uC885\uC131 \uC0C1\uD0DC \uBCC0\uD654\uD568\uC218 START ***********************/
	// \uC218\uC815\uD544\uC694
	int moveJong(int num) {
		if (0 <= jongState_ && jongState_ <= 34) {
			jongState_ = jongTransTable[jongState_][num];
			state_ = 3;
			if (jongState_ < 0) { // jongState_\uAC00 0\uBCF4\uB2E4
									// \uC791\uB2E4\uBA74 \uC5D0\uB7EC ->
									// \uB9AC\uC14B,
									// \uC5D0\uB7EC\uBA54\uC138\uC9C0\uB97C
									// \uBCF4\uB0B8\uB2E4.
				reset();
				setError();
			} else
				jong_.setText("" + jongSungs_.charAt(jongState_));
		} else {
			reset();
			setError();
		}
		return jongState_;
	}

	/************************* \uC885\uC131 \uC0C1\uD0DC \uBCC0\uD654\uD568\uC218 END ***********************/

	/************* \uC785\uB825\uC774 \uC798\uBABB\uB418\uC5C8\uC744 \uACBD\uC6B0 ***********/
	void setError() {
		this.setBackground(Color.red);
	}

	/************************************************/

	/**************** \uCC98\uC74C\uC0C1\uD0DC\uB85C \uB3CC\uC544\uAC10, \uC9C0\uC6B0\uAE30 *******************/
	void reset() {
		record_.setText("");
		cho_.setText("");
		jung_.setText("");
		jong_.setText("");
		jongState_ = 0;
		jungState_ = 0;
		choState_ = 0;
		state_ = 0;
	}

	/**************** \uCC98\uC74C\uC0C1\uD0DC\uB85C \uB3CC\uC544\uAC10, \uC9C0\uC6B0\uAE30 *******************/

	/**
	 * \uD074\uB798\uC2A4, ExitLIstener \uC708\uB3C4\uC6B0 event Listener
	 */
	class ExitListener implements WindowListener {
		public void windowClosing(WindowEvent e) {
			System.out.println("bye!!");
			System.exit(0);
		}

		public void windowActivated(WindowEvent e) {
		}

		public void windowClosed(WindowEvent e) {
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowOpened(WindowEvent e) {
		}
	}

	// \uD604\uC7AC \uCD08\uC131, \uC911\uC131\uC774
	// \uBC1B\uC544\uB4E4\uC5EC\uC9C4 \uC0C1\uD0DC\uC778\uAC00\uB97C
	// \uB098\uD0C0\uB0C4
	int state_ = 0;
	int jong_change_cho; // \uC885\uC131\uC744 \uCD08\uC131\uC73C\uB85C
							// \uBCC0\uD658

	class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("\uC9C0\uC6B0\uAE30")) {
				reset();
				setBackground(Color.lightGray);
				return;
			}

			/*******
			 * \uC0AC\uC6A9\uC790\uAC00 \uC785\uB825\uD55C \uBC84\uD2BC\uC744
			 * \uC815\uC218\uD615\uC73C\uB85C \uBCC0\uD658
			 ************/
			int num = 0;
			if (e.getActionCommand() == "\u3131")
				num = 0;
			else if (e.getActionCommand() == "\u3134")
				num = 1;
			else if (e.getActionCommand() == "-")
				num = 2;
			else if (e.getActionCommand() == "\u3145")
				num = 3;
			else if (e.getActionCommand() == "\u314F")
				num = 4;
			else if (e.getActionCommand() == "\u3153")
				num = 5;
			else if (e.getActionCommand() == "\u3161")
				num = 6;
			else if (e.getActionCommand() == "\u3163")
				num = 7;
			/********************************************************/

			record_.setText(record_.getText() + e.getActionCommand());
			// \uC0C1\uD0DC 0 : \uCC98\uC74C \uC2DC\uC791\uD55C \uC0C1\uD0DC,
			// \uC544\uC9C1 \uCD08\uC131\uC774 \uB098\uC624\uC9C0 \uC54A\uC740
			// \uC0C1\uD0DC
			// \uC0C1\uD0DC 1 : \uCD08\uC131\uC774 \uB098\uC628 \uC0C1\uD0DC.
			// \uCD08\uC131\uC774 \uC138\uBC88 \uB354 \uB098\uC62C \uC218
			// \uC788\uB2E4.
			// \uC0C1\uD0DC 2 : \uC911\uC131\uC774 \uB098\uC628 \uC0C1\uD0DC.
			// \uC911\uC131\uC774 \uC138\uBC88 \uB354 \uB098\uC62C \uC218
			// \uC788\uB2E4.
			// \uC0C1\uD0DC 3 : \uC885\uC131\uC774 \uB098\uC628 \uC0C1\uD0DC.
			// \uC885\uC131\uC774 \uC138\uBC88 \uB354 \uB098\uC62C \uC218
			// \uC788\uB2E4.
			// \uC774\uD6C4 \uC911\uC131\uC774\uB098 \uC885\uC131\uC774 \uB354
			// \uB098\uC628\uB2E4\uBA74 \uC0C1\uD0DC\uB97C 1\uB85C
			// \uB9CC\uB4E0\uB2E4.

			// \uC0C1\uD0DC\uAC00 0\uC774\uB098 1\uC778\uB370 \uCD08\uC131\uC774
			// \uB098\uC628 \uACBD\uC6B0
			if ((state_ == 0 || state_ == 1) && (isCho(num))) {
				moveCho(num);
				// \uC0C1\uD0DC\uAC00 1\uC778\uB370 \uC911\uC131\uC774
				// \uB098\uC628 \uACBD\uC6B0
			} else if (state_ == 1 && (!isCho(num))) {
				moveJung(num);
				// \uC0C1\uD0DC\uAC00 2\uC778\uB370 \uC911\uC131\uC774
				// \uB098\uC628 \uACBD\uC6B0
				// \uC911\uC131\uC744 \uB354 \uBC1B\uC544\uB4E4\uC774\uBA74
				// \uB428
			} else if (state_ == 2 && !isCho(num)) {
				moveJung(num);
			}
			// \uC0C1\uD0DC\uAC00 2\uC778\uB370 \uC790\uC74C\uC774 \uB098\uC628
			// \uACBD\uC6B0 -> \uC885\uC131
			else if (state_ == 2 && isCho(num)) {
				jong_change_cho = moveJong(num); // \uC885\uC131\uC758
													// \uC0C1\uD0DC\uAC12
													// \uC800\uC7A5,
													// \uD6C4\uC5D0
													// \uBAA8\uC74C\uC774
													// \uB098\uC624\uBA74
													// \uCD08\uC131\uC73C\uB85C
													// \uBCC0\uD658
			}
			// \uC0C1\uD0DC\uAC00 2\uC778\uB370 \uC790\uC74C\uC774 \uB098\uC628
			// \uACBD\uC6B0 -> \uC885\uC131, \uCD08\uC131
			else if (state_ == 3 && isCho(num)) {
				jong_change_cho = moveJong(num); // \uC885\uC131\uC758
													// \uC0C1\uD0DC\uAC12
													// \uC800\uC7A5,
													// \uD6C4\uC5D0
													// \uBAA8\uC74C\uC774
													// \uB098\uC624\uBA74
													// \uCD08\uC131\uC73C\uB85C
													// \uBCC0\uD658
				if (jong_change_cho > 22) {
					if (jong_change_cho == 23)
						jong_change_cho = 4;
					if (jong_change_cho == 24)
						jong_change_cho = 11;
					if (jong_change_cho == 25)
						jong_change_cho = 22;
					if (jong_change_cho == 26)
						jong_change_cho = 1;
					if (jong_change_cho == 27)
						jong_change_cho = 7;
					if (jong_change_cho == 28)
						jong_change_cho = 14;
					if (jong_change_cho == 29)
						jong_change_cho = 4;
					if (jong_change_cho == 30)
						jong_change_cho = 3;
					if (jong_change_cho == 31)
						jong_change_cho = 16;
					if (jong_change_cho == 32)
						jong_change_cho = 21;
					if (jong_change_cho == 33)
						jong_change_cho = 22;
					if (jong_change_cho == 34)
						jong_change_cho = 4;
				}
			}
			// \uC0C1\uD0DC\uAC00 3\uC778\uB370 \uBAA8\uC74C\uC774 \uB098\uC628
			// \uACBD\uC6B0 -> \uC911\uC131, \uC55E\uC758 \uC885\uC131\uC744
			// \uCD08\uC131\uC73C\uB85C \uBCC0\uD658\uD574\uC57C \uD55C\uB2E4
			else if (state_ == 3 && !isCho(num)) {
				reset();
				cho_.setText("" + choSungs_.charAt(jong_change_cho));
				moveJung(num);
			}
			// \uADF8 \uC774\uC678\uC758 \uACBD\uC6B0\uB294 \uC5D0\uB7EC\uC784
			// (\uC608\uB97C \uB4E4\uC5B4 \uC0C1\uD0DC\uAC00 0\uC778\uB370
			// \uC911\uC131\uC774 \uB098\uC628 \uACBD\uC6B0)
			else {
				reset();
				setError();
			}
		}

		boolean isCho(int n) {
			if (4 <= n && n <= 7)
				return false; // \uBC84\uD2BC \u314F,\u3153,\u3163,\u3161\uB97C
								// \uC120\uD0DD\uD558\uAC8C \uB418\uBA74
								// \uCD08\uC131\uC774 \uC544\uB2D8
			return true;
		}
	}
}
