package com.battle.planet.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.battle.planet.BattleLevel;
import com.battle.planet.projectiles.BasicProjectile;
import com.battle.planet.projectiles.Projectile;
import com.battle.planet.projectiles.VenusRingProjectile;

public class Venus extends Enemy {

    private int phase = 0;

    /*Controls cooldown for all non-specific spawning abilities
    Phase 1: Summons an acid seeker cloud
    Phase 2: Not Used
    Phase 3: Summons an acid cloud from a random offscreen position, with velocity toward the getLevel() center
    Phase 4: Not Used
    */
    private float spawnCooldown;
    private final float SPAWN_MAX_COOLDOWN = 3f;
    //Counts how many enemies are spawned (for shielding purposes)
    private int spawned = 0;

    /*For any aimed abilities.
    Phase 1: Not Used
    Phase 2: Spawns acid clouds from offscreen to straight down toward the player.
    Phase 3: Not Used
    Phase 4: Aims hearts towards the player.
     */
    private float aimCooldown;
    private final float AIM_MAX_COOLDOWN = 1f;

    /* Controls 'essential' attacks
    Phase 1: Not Used
    Phase 2: Used to periodically drop a bunch of acid clouds
    Phase 3: Controls a constant wave spread pattern
    Phase 4: Controls constant acid rain
     */
    private float mainCooldown;
    private final float MAIN_MAX_COOLDOWN = 5f;

    private float aimDirection = 3 * MathUtils.PI / 2;
    private float aimChange = MathUtils.PI / 12;

    public Venus(final BattleLevel lev, float x, float y) {
        super(lev, x, y, 70, 80, 500);

        spawnCooldown = SPAWN_MAX_COOLDOWN;
        aimCooldown = AIM_MAX_COOLDOWN;
        mainCooldown = MAIN_MAX_COOLDOWN;

        addPhaseMarkers(400, 300, 200, 100);
    }

    @Override
    public void drawBody(ShapeRenderer r) {
        if (spawned == 0) {
            r.setColor(Color.GOLDENROD);
        } else {
            r.setColor(Color.valueOf("#c1a868"));
        }
        r.circle(hitbox.x, hitbox.y, hitbox.radius);
    }

    @Override
    public void drawObjects(ShapeRenderer r) {
        float x = getPlayer().getCenterX();
        float y = getPlayer().getCenterY();
        if (phase == -1) {
            r.setColor(Color.YELLOW);
            r.rectLine(hitbox.x, hitbox.y, hitbox.x, 550, 5);
        }
    }

    @Override
    public Array<Projectile> attack(float frame) {
        float x = getPlayer().getCenterX();
        float y = getPlayer().getCenterY();
        clearProjectiles();
        if (phase == 1) {
            spawnCooldown -= frame;
            if (spawnCooldown <= 0 && spawned < 3) {
                spawnCooldown = SPAWN_MAX_COOLDOWN;
                setCanSpawn(true);
            }
        }
        if (phase == 2) {
            if (hitbox.y < 550) {
                float direction = MathUtils.atan2(550 - hitbox.y, 300 - hitbox.x);
                hitbox.x += MathUtils.cos(direction) * 200 * frame;
                hitbox.y += MathUtils.sin(direction) * 200 * frame;
            } else {
                mainCooldown -= frame;
                aimCooldown -= frame;
                if (aimCooldown <= 0) {
                    setCanSpawn(true);
                }
                if (mainCooldown <= 0) {
                    setCanSpawn(true);
                }
            }
        }
        if (phase == 3) {
            mainCooldown -= frame;
            spawnCooldown -= frame;
            if (spawnCooldown <= 0) {
                //More frequent spawning based on health
                //~50% cooldown at 201 health.
                spawnCooldown = (SPAWN_MAX_COOLDOWN * ((getHealth() - 200) / 200) + 0.5f);
                setCanSpawn(true);
            }
            //Creates spreads around an open area
            if (mainCooldown <= 0) {
                mainCooldown = MAIN_MAX_COOLDOWN * 0.03f;
                createSpread(aimDirection + MathUtils.PI / 6, 15, MathUtils.PI / 6, 120);
                createSpread(aimDirection - MathUtils.PI / 6, 15, MathUtils.PI / 6, 120);
                //Side spread
                createSpread(MathUtils.PI, 15, MathUtils.PI / 6, 120);
                createSpread(0, 15, MathUtils.PI / 6, 120);
            }
            aimDirection += aimChange * frame;
            if (aimDirection < 4 * MathUtils.PI / 3) {
                aimDirection = 4 * MathUtils.PI / 3;
                aimChange *= -1;
            } else if (aimDirection > 5 * MathUtils.PI / 3) {
                aimDirection = 5 * MathUtils.PI / 3;
                aimChange *= -1;
            }
        }
        if (phase == 4) {
            mainCooldown -= frame;
            aimCooldown -= frame;
            //spawnCooldown -= frame;
            //Constant acid rain
            if (mainCooldown <= 0) {
                mainCooldown = MAIN_MAX_COOLDOWN * 0.03f;
                addProjectile(new BasicProjectile(getLevel(), MathUtils.random(100, 700), 700, -MathUtils.random(10, 70), -MathUtils.random(170, 200)));
                //addProjectile(new BasicProjectile(MathUtils.random(100, 700), 700, -MathUtils.random(10, 70), -MathUtils.random(170, 200)));
            }
            //Hearts
            if (aimCooldown <= 0) {
                float angle = MathUtils.atan2(getPlayer().getCenterY() - hitbox.y, getPlayer().getCenterX() - hitbox.x);
                createHeart(hitbox.x, hitbox.y, angle, 10, MathUtils.cos(angle) * 150, MathUtils.sin(angle) * 150);
                aimCooldown = AIM_MAX_COOLDOWN * 2;
            }
        }
        if (phase == 5) {
            mainCooldown -= frame;
            aimCooldown -= frame;
            spawnCooldown -= frame;

            //Constant acid rain (still)
            if (mainCooldown <= 0) {
                mainCooldown = MAIN_MAX_COOLDOWN * 0.06f;
                addProjectile(new BasicProjectile(getLevel(), MathUtils.random(100, 700), 700, -MathUtils.random(10, 70), -MathUtils.random(170, 200)));
                //addProjectile(new BasicProjectile(MathUtils.random(100, 700), 700, -MathUtils.random(10, 70), -MathUtils.random(170, 200)));
            }

            if (spawnCooldown <= 0) {
                setCanSpawn(true);
                spawnCooldown = SPAWN_MAX_COOLDOWN * 1.5f;
            }
        }


        if (phase == 0) {
            //Create orbital ring
            for (int i = 0; i < 72; ++i) {
                float angle = (float) (i/72.0) * 6.28f;
                addProjectile(new VenusRingProjectile(getLevel(), 150,300 + 200 * MathUtils.cos(angle), 300 + 200 * MathUtils.sin(angle), 1.0f, 3.14f, this));
            }
            phase = 1;
        }
        if (phase == -1) {
            mainCooldown -= frame;
            if (mainCooldown <= 0) {
                //mainCooldown = MAIN_MAX_COOLDOWN;
                phase = 2;
            }
        }

        //Phase transitions
        if (phase == 1 && getHealth() <= 400) {
            phase = -1;
            mainCooldown = 0.7f;
        }
        if ((phase == 2 || phase == -1) && getHealth() <= 300) {
            mainCooldown = MAIN_MAX_COOLDOWN / 10;
            phase = 3;
        }
        if (phase == 3 && getHealth() <= 200) {
            phase = 4;
            setCanSpawn(true);
        }
        if (phase == 4 && getHealth() <= 100) {
            phase = 5;
        }
        return getBullets();
    }

    @Override
    public Array<Enemy> spawn(float frame) {
        float x = getPlayer().getCenterX();
        float y = getPlayer().getCenterY();
        Array<Enemy> enemies = new Array<Enemy>();
        if (phase == 1) {
            int rand = MathUtils.random(0, 3);
            //Spawn from random sides
            if (rand == 0) {
                enemies.add(new AcidSeeker(getLevel(), this, MathUtils.random(0, 600), 670));
            } else if (rand == 1) {
                enemies.add(new AcidSeeker(getLevel(), this, MathUtils.random(0, 600), -70));
            } else if (rand == 2) {
                enemies.add(new AcidSeeker(getLevel(), this, 670, MathUtils.random(0, 600)));
            } else if (rand == 3) {
                enemies.add(new AcidSeeker(getLevel(), this, -70, MathUtils.random(0, 600)));
            }
            setCanSpawn(false);
            spawned += 1;
        }
        if (phase == 2) {
            if (aimCooldown <= 0) {
                enemies.add(new AcidCloud(getLevel(), MathUtils.random(x-150, x+150), 700, 0, -200));
                aimCooldown = AIM_MAX_COOLDOWN/2;
                setCanSpawn(false);
            }
            if (mainCooldown <= 0) {
                for (int i = 0; i < 25; ++i) {
                    enemies.add(new AcidCloud(getLevel(), MathUtils.random(20, 580), MathUtils.random(620, 749), 0, -MathUtils.random(175, 200)));
                }
                mainCooldown = MAIN_MAX_COOLDOWN;
                setCanSpawn(false);
            }
        }
        if (phase == 3) {
            float angle = MathUtils.random(360) * MathUtils.degreesToRadians;
            enemies.add(new AcidCloud(getLevel(), 300 + MathUtils.cos(angle) * 400, 300 + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 150, -MathUtils.sin(angle) * 150));
            setCanSpawn(false);
        }
        if (phase == 4) {
            enemies.add(new VenusReflector(getLevel(), this, hitbox.radius + 40, 1000, 0, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(getLevel(), this, hitbox.radius + 40, 1000, MathUtils.PI / 12, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(getLevel(), this, hitbox.radius + 40, 1000, -MathUtils.PI / 12, MathUtils.PI / 2, true));

            enemies.add(new VenusReflector(getLevel(),  this, hitbox.radius + 40, 1000, MathUtils.PI, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(getLevel(),  this, hitbox.radius + 40, 1000, 11 * MathUtils.PI / 12, MathUtils.PI / 2, true));
            enemies.add(new VenusReflector(getLevel(),  this, hitbox.radius + 40, 1000, 13 * MathUtils.PI / 12, MathUtils.PI / 2, true));
            setCanSpawn(false);
        }
        if (phase == 5) {
            float angle = MathUtils.random(360) * MathUtils.degreesToRadians;
            for (int i = 0; i < 6; ++i) {
                angle += MathUtils.PI/3;
                enemies.add(new AcidCloud(getLevel(), 300 + MathUtils.cos(angle) * 400, 300 + MathUtils.sin(angle) * 400, -MathUtils.cos(angle) * 100, -MathUtils.sin(angle) * 100));
            }
            setCanSpawn(false);
        }
        /*
        if (phase == -1) {
            for (int i = 0; i < 8; ++i) {
                enemies.add(new VenusShield(130, 0.785398f * i, 0.785398f, this));
                enemies.add(new VenusSniper(100, 0.785398f * i, 0.785398f, this));
            }
            spawned += 16;
            setCanSpawn(false);
            phase = 2;
        }
         */
        return enemies;
    }

    @Override
    public Array<Projectile> collide(Array<Projectile> projectiles) {
        if (spawned == 0) {
            for (Projectile p : projectiles) {
                if (!p.isDestroyed && this.hitbox.contains(p.hitbox.x, p.hitbox.y)) {
                    p.isDestroyed = true;
                    modHealth(-1);
                }
            }
        }
        return projectiles;
    }

    @Override
    public void drawHealthBars(ShapeRenderer r) {
        //Invincibility gets different colored healthbar
        if (spawned > 0) {
            r.setColor(Color.DARK_GRAY);
            r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2, 10);
            r.setColor(Color.valueOf("#68d9ff"));
            r.rect(hitbox.x - hitbox.radius, hitbox.y - hitbox.radius, hitbox.radius * 2 * (getHealth() / getMaxHealth()), 10);
        } else {
            super.drawHealthBars(r);
        }
    }

    public void createHeart(float x, float y, float angle, float scale, float vx, float vy) {
        float s = MathUtils.sin(angle + MathUtils.PI / 2);
        float c = MathUtils.cos(angle + MathUtils.PI / 2);

        //Top and bottom
        addProjectile(new BasicProjectile(getLevel(), x + rotateX(0, 2 * scale, c, s), y + rotateY(0, 2 * scale, c, s), vx, vy));
        addProjectile(new BasicProjectile(getLevel(), x + rotateX(0, -4 * scale, c, s), y + rotateY(0, -4 * scale, c, s), vx, vy));

        //Heart halves, negative for left, positive for right.
        for (int i = -1; i < 2; i += 2) {

            addProjectile(new BasicProjectile(getLevel(), x + rotateX(1 * scale * i, 2.5f * scale, c, s), y + rotateY( 1 * scale * i, 2.5f * scale, c, s), vx, vy));
            addProjectile(new BasicProjectile(getLevel(), x + rotateX(2 * scale * i, 3 * scale, c, s), y + rotateY(2 * scale * i, 3 * scale, c, s), vx, vy));
            addProjectile(new BasicProjectile(getLevel(), x + rotateX(3 * scale * i, 2.5f * scale, c, s), y + rotateY(3 * scale * i, 2.5f * scale, c, s), vx, vy));
            addProjectile(new BasicProjectile(getLevel(), x + rotateX(3.5f * scale * i, 2 * scale, c, s), y + rotateY(3.5f * scale * i, 2 * scale, c, s), vx, vy));
            addProjectile(new BasicProjectile(getLevel(), x + rotateX(4 * scale * i, 1 * scale, c, s), y + rotateY(4 * scale * i, 1 * scale, c, s), vx, vy));

            addProjectile(new BasicProjectile(getLevel(), x + rotateX(4 * scale * i, 0, c, s), y + rotateY(4 * scale * i, 0, c, s), vx, vy));
            addProjectile(new BasicProjectile(getLevel(), x + rotateX(3 * scale * i,-1 * scale, c, s), y + rotateY(3 * scale * i, -1 * scale, c, s), vx, vy));
            addProjectile(new BasicProjectile(getLevel(), x + rotateX(2 * scale * i,-2 * scale, c, s), y + rotateY(2 * scale * i, -2 * scale, c, s), vx, vy));
            addProjectile(new BasicProjectile(getLevel(), x + rotateX(1 * scale * i,-3 * scale, c, s), y + rotateY(1 * scale * i, -3 * scale, c, s), vx, vy));
        }
    }

    public float rotateX(float x, float y, float c, float s) {
        return x * c - y * s;
    }

    public float rotateY(float x, float y, float c, float s) {
        return y * c + x * s;
    }

    public void reduceSpawned() {
        spawned--;
    }

    public int getPhase() {
        return phase;
    }
}
