CREATE TABLE IF NOT EXISTS users(id BIGSERIAL PRIMARY KEY,email VARCHAR(180) UNIQUE NOT NULL,password VARCHAR(255) NOT NULL,name VARCHAR(180),phone TEXT,address TEXT,postal_code TEXT,state TEXT,municipality TEXT,settlement TEXT,document_number TEXT,role VARCHAR(20) NOT NULL DEFAULT 'USER',deleted BOOLEAN NOT NULL DEFAULT FALSE);
CREATE TABLE IF NOT EXISTS audit_log(id BIGSERIAL PRIMARY KEY,user_id BIGINT,action VARCHAR(255),ip VARCHAR(80),created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS documents(id BIGSERIAL PRIMARY KEY,user_id BIGINT NOT NULL REFERENCES users(id),file_name VARCHAR(255) NOT NULL,content_type VARCHAR(120),encrypted_content BYTEA NOT NULL,deleted BOOLEAN NOT NULL DEFAULT FALSE,created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS registration_counter(id INT PRIMARY KEY DEFAULT 1,total BIGINT NOT NULL DEFAULT 0);
INSERT INTO registration_counter(id,total) VALUES(1,0) ON CONFLICT(id) DO NOTHING;
CREATE OR REPLACE FUNCTION increment_registration_counter() RETURNS TRIGGER AS $$ BEGIN UPDATE registration_counter SET total=total+1 WHERE id=1; RETURN NEW; END; $$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS trg_user_registration ON users; CREATE TRIGGER trg_user_registration AFTER INSERT ON users FOR EACH ROW EXECUTE FUNCTION increment_registration_counter();
CREATE OR REPLACE PROCEDURE soft_delete_user(p_id BIGINT) LANGUAGE plpgsql AS $$ BEGIN UPDATE users SET deleted=TRUE WHERE id=p_id; END; $$;
CREATE TABLE IF NOT EXISTS password_reset_tokens(id BIGSERIAL PRIMARY KEY,user_id BIGINT NOT NULL REFERENCES users(id),token VARCHAR(120) UNIQUE NOT NULL,expires_at TIMESTAMP NOT NULL,used BOOLEAN NOT NULL DEFAULT FALSE);

-- Catálogo postal local. Se puede poblar con el TXT oficial SEPOMEX sin cambiar el código.
CREATE TABLE IF NOT EXISTS postal_codes(postal_code VARCHAR(5) NOT NULL,state VARCHAR(120) NOT NULL,municipality VARCHAR(160) NOT NULL,settlement VARCHAR(180) NOT NULL,PRIMARY KEY(postal_code,settlement));
CREATE INDEX IF NOT EXISTS idx_postal_codes_cp ON postal_codes(postal_code);
-- Registros de demostración; para cobertura nacional importar el catálogo oficial SEPOMEX.
INSERT INTO postal_codes VALUES
('91810','Veracruz de Ignacio de la Llave','Veracruz','Playa Linda'),
('91919','Veracruz de Ignacio de la Llave','Veracruz','Las Bajadas'),
('06000','Ciudad de México','Cuauhtémoc','Centro (Área 1)'),
('44100','Jalisco','Guadalajara','Guadalajara Centro'),
('64000','Nuevo León','Monterrey','Monterrey Centro') ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS mail_settings(id INT PRIMARY KEY,provider VARCHAR(40),sender_email VARCHAR(180),host VARCHAR(180),port INT,encrypted_password TEXT,auth BOOLEAN NOT NULL DEFAULT TRUE,starttls BOOLEAN NOT NULL DEFAULT TRUE);
