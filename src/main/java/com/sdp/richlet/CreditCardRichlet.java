package com.sdp.richlet;

import org.zkoss.zk.ui.GenericRichlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Style;

import com.sdp.controller.ShowCardComposer;

public class CreditCardRichlet extends GenericRichlet {

	static final String LABEL="label";
	static final String VALUE="value";
    @Override
    public void service(Page page) throws Exception {
        Div wrap = new Div();
        wrap.setSclass("wrap root");
        wrap.setPage(page);

        Style style = new Style();
        style.setContent("""
            :root {
                --bg: #0f172a;
                --text: #ffffff;
                --muted: #cbd5e1;
                --radius: 18px;
            }

            body, .root { background: linear-gradient(135deg, #0f172a, #1e293b); }
            .wrap { min-height: 100vh; display: grid; place-items: center; padding: 28px; }

            /* Flip Container */
            .flip-card { width: 420px; height: 260px; perspective: 1000px; }
            .flip-inner {
                position: relative; width: 100%; height: 100%;
                text-align: center; transition: transform 0.8s;
                transform-style: preserve-3d;
            }
            .flip-card:hover .flip-inner { transform: rotateY(180deg); }

            /* Common Card Styles */
            .card {
                position: absolute; width: 100%; height: 100%;
                border-radius: var(--radius);
                padding: 24px 28px;
                color: var(--text);
                background: linear-gradient(145deg, #1e293b, #0f172a);
                box-shadow: 0 12px 30px rgba(0,0,0,.6), inset 0 0 0 1px rgba(255,255,255,.05);
                backface-visibility: hidden;
                overflow: hidden;
            }
            .card::before {
                content: "";
                position: absolute; top: -50px; right: -50px;
                width: 200px; height: 200px;
                background: radial-gradient(circle, rgba(255,255,255,0.05), transparent);
                border-radius: 50%;
            }

            /* Front */
            .front .card-top { display: flex; align-items: center; justify-content: space-between; }
            .brand { font-size: 20px; font-weight: bold; color: #e2e8f0; }
            .chip { width: 50px; height: 38px; border-radius: 6px; background: linear-gradient(145deg,#d1d5db,#9ca3af); margin: 18px 0; }
            .number { font-family: "OCR A Std", monospace; letter-spacing: 2px; font-size: 20px; margin: 14px 0; }
            .row { display: flex; justify-content: space-between; align-items: center; }
            .label { font-size: 10px; color: var(--muted); text-transform: uppercase; letter-spacing: .5px; }
            .value { font-size: 14px; font-weight: 600; }
            .mastercard {
                width: 50px; height: 30px;
                background: url('https://upload.wikimedia.org/wikipedia/commons/0/04/Mastercard-logo.png') no-repeat center/contain;
            }
            .contactless {
                width: 22px; height: 22px;
                background: url('https://upload.wikimedia.org/wikipedia/commons/2/2b/Contactless_Card_Symbol.svg') no-repeat center/contain;
            }

            /* Back */
            .back { transform: rotateY(180deg); }
            .stripe { background: #000; height: 45px; margin-top: 20px; border-radius: 4px; }
            .cvv-box {
                background: #fff; color: #000; font-family: monospace;
                width: 90px; padding: 6px; border-radius: 4px;
                margin: 20px 0 10px auto; text-align: right; font-weight: bold;
            }
            .limit { margin-top: 20px; text-align: left; font-size: 14px; }
            """);
        style.setParent(wrap);

        Div flipCard = new Div();
        flipCard.setSclass("flip-card");
        flipCard.setParent(wrap);

        Div flipInner = new Div();
        flipInner.setSclass("flip-inner");
        flipInner.setParent(flipCard);

        Div frontCard = new Div();
        frontCard.setSclass("card front");
        frontCard.setParent(flipInner);

        Div cardTop = new Div();
        cardTop.setSclass("card-top");
        cardTop.setParent(frontCard);

        Label brand = new Label("SDP Horizon Bank");
        brand.setSclass("brand");
        brand.setParent(cardTop);

        Div contactless = new Div();
        contactless.setSclass("contactless");
        contactless.setParent(cardTop);

        Div chip = new Div();
        chip.setSclass("chip");
        chip.setParent(frontCard);

        Label previewNumber = new Label();
        previewNumber.setSclass("number");
        previewNumber.setParent(frontCard);

        Separator sepFrontSpace = new Separator();
        sepFrontSpace.setSpacing("28px");
        sepFrontSpace.setParent(frontCard);

        Div row = new Div();
        row.setSclass("row");
        row.setParent(frontCard);

        Div validBlock = new Div();
        validBlock.setParent(row);

        Label validLbl = new Label("Valid Thru");
        validLbl.setSclass(LABEL);
        validLbl.setParent(validBlock);

        Separator validSep = new Separator();
        validSep.setParent(validBlock);

        Label previewExpiry = new Label();
        previewExpiry.setSclass(VALUE);
        previewExpiry.setParent(validBlock);

        Div holderBlock = new Div();
        holderBlock.setParent(row);

        Label holderLbl = new Label("Card Holder");
        holderLbl.setSclass(LABEL);
        holderLbl.setParent(holderBlock);

        Separator holderSep = new Separator();
        holderSep.setParent(holderBlock);

        Label previewName = new Label();
        previewName.setSclass(VALUE);
        previewName.setParent(holderBlock);

        Div mcLogo = new Div();
        mcLogo.setSclass("mastercard");
        mcLogo.setParent(row);

        Div backCard = new Div();
        backCard.setSclass("card back");
        backCard.setParent(flipInner);

        Div stripe = new Div();
        stripe.setSclass("stripe");
        stripe.setParent(backCard);

        Div cvvBox = new Div();
        cvvBox.setSclass("cvv-box");
        cvvBox.setParent(backCard);

        Label previewCVV = new Label();
        previewCVV.setParent(cvvBox);

        Div limitBlock = new Div();
        limitBlock.setSclass("limit");
        limitBlock.setParent(backCard);

        Label limitLbl = new Label("Credit Limit");
        limitLbl.setSclass(LABEL);
        limitLbl.setParent(limitBlock);

        Label previewLimit = new Label();
        previewLimit.setSclass(VALUE);
        previewLimit.setParent(limitBlock);

        ShowCardComposer composer = new ShowCardComposer();
        composer.setPreviewNumber(previewNumber);
        composer.setPreviewName(previewName);
        composer.setPreviewExpiry(previewExpiry);
        composer.setPreviewCVV(previewCVV);
        composer.setPreviewLimit(previewLimit);

        composer.loadCard(); 
    }
}
