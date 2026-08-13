INSERT INTO training_types (id, name) VALUES (1, 'individual'), (2, 'group')
ON CONFLICT (id) DO NOTHING;

alter table trainee_traineer drop constraint if exists trainee_foreign_key;
alter table trainee_traineer drop constraint if exists trainer_foreign_key;

alter table trainee_traineer drop constraint if exists new_constraint_trainees;
alter table trainee_traineer drop constraint if exists new_constraint_trainers;

alter table trainee_traineer add constraint  new_constraint_trainees foreign key (trainee_id) references trainees(id) on delete cascade;
alter table trainee_traineer add constraint  new_constraint_trainers foreign key (trainer_id)  references trainers(id) on delete cascade;