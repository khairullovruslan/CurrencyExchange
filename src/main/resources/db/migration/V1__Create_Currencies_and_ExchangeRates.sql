create table Currencies(
                           id serial primary key,
                           code varchar(255) unique ,
                           full_name varchar(255),
                           sign varchar(255)
);

create table ExchangeRates(
                              id serial  primary key,
                              BaseCurrencyId int unique ,
                              TargetCurrencyId int unique ,
                              rate decimal(6),
                              foreign key (BaseCurrencyId) references currencies(id),
                              foreign key (TargetCurrencyId) references currencies(id)
);

CREATE UNIQUE INDEX idx_unique_exchange_rate
    ON ExchangeRates (BaseCurrencyId, TargetCurrencyId);

