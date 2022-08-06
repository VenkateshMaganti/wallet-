package com.epam.rd.autotasks.wallet;

import java.util.List;

/**
 * Wallet creator. Is used to create a {@linkplain Wallet} instance.
 * <p/>
 * You need to specify your implementation in {@linkplain #wallet(List, PaymentLog)} method.
 */
public final class WalletFactory {

    private WalletFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates new {@linkplain Wallet} instance and passes {@code accounts} and {@code log} to it.
     * <p/>
     * You must return your implementation here.
     *
     * @param accounts which will be used for payments
     * @param log which will be used to log payments
     * @return new {@linkplain Wallet} instance
     */
    public static Wallet wallet(List<Account> accounts, PaymentLog log) {
        //throw new UnsupportedOperationException();
        return new Wallet() {
            @Override
            public void pay(String recipient, long amount) throws Exception {
                boolean foundAmount = false;
                for (Account account : accounts) {
                    account.lock().lock();
                    if (account.balance() >= 0 && account.balance() >= amount) {
                        foundAmount = true;
                        account.pay(amount);
                        log.add(account, recipient, amount);
                    }
                    account.lock().unlock();
                    if (foundAmount) {
                        break;
                    }
                }
                if (!foundAmount) {
                    throw new ShortageOfMoneyException(recipient, amount);
                }
            }
        };
    }
}
