insert into tb_entrega
(id, cpf_entregador, cep_raiz, status)
values
    ('70fc4381-5ddd-4181-ae94-8d05dfaa4b69', '71973751003', '95880', 'ENTREGUE'),
    ('a0839d7b-a7b4-49c6-be5d-a1c9ff2801b4', '86655281059', '95870', 'ENTREGUE'),
    ('e7bdc094-b8b8-4495-b8fb-731f12c24658', '63088406027', '90250', 'ENTREGUE');

insert into tb_pedido
(id, id_entrega, id_pedido)
values
    ('2fb60bfa-22bd-4fe1-8189-8d10cb707819', '70fc4381-5ddd-4181-ae94-8d05dfaa4b69', '353bc1d6-2028-47c8-b667-8c7ff5ba7d62'),
    ('0b0e452d-e843-47a2-a579-227705325981', '70fc4381-5ddd-4181-ae94-8d05dfaa4b69', 'bdad9dfc-4eb5-472c-a807-64ceea8b5de6'),
    ('6fb300b3-079f-4e96-8894-0fcdfb75b913', '70fc4381-5ddd-4181-ae94-8d05dfaa4b69', '850d8bee-7ace-4b7b-a3ab-9a2d15de14a1'),

    ('935940d4-0cf2-401f-b113-9c251b417925', 'e7bdc094-b8b8-4495-b8fb-731f12c24658', '7eb0fa7c-d14b-4fb9-9f3f-e8c8b961db5e'),
    ('5734ae85-b7d1-4323-bf2a-de49664aba07', 'e7bdc094-b8b8-4495-b8fb-731f12c24658', '8c561ff2-5cde-449e-be60-654e0f745b43'),
    ('0da2cd98-d80d-4073-8612-9e7b5737c591', 'e7bdc094-b8b8-4495-b8fb-731f12c24658', '9abb344d-771c-40d1-a733-dd5af93699bc'),

    ('5312498e-25ed-4946-9a9a-809011d41053', 'a0839d7b-a7b4-49c6-be5d-a1c9ff2801b4', 'd5bb6050-dfb9-429e-948e-7b3657eb00ac');