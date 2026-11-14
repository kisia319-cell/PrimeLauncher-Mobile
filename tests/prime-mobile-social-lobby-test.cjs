const fs=require("node:fs");const path=require("node:path");const assert=require("node:assert/strict");
const read=p=>fs.readFileSync(path.join(process.cwd(),p),"utf8");
const base="app/src/main/assets/prime/",html=read(base+"index.html"),world=read(base+"prime-world.js"),online=read(base+"prime-online.js");
for(const id of ["friends-page","prime-friend-search","prime-friend-find","prime-friend-list","prime-lobby-invites","prime-lobby-create","prime-lobby-panel","prime-play-bots"])assert.ok(html.includes(id),"Missing Android Prime social UI: "+id);
for(const method of ["friends","searchPlayers","addFriend","respondFriend","lobby","createLobby","inviteLobbyFriend","respondLobbyInvite","startLobby","leaveLobby"])assert.ok(online.includes(method+":"),"Missing Android Prime client method: "+method);
assert.ok(html.includes("Играть против ботов")&&world.includes("НАЧАТЬ ДУЭЛЬ 1 × 1"),"Android must expose both bot and friend-duel flows");
console.log("Android Prime social/lobby integration: all assertions passed");
assert.match(html,/id="registration-page"/,'Mobile Prime must include a dedicated registration page');
assert.match(html,/У вас есть аккаунт SkyRealm\?/,'Mobile registration must ask whether the player already has an account');
assert.match(world,/prime-account-yes/,'Existing-account choice must open sign-in');
assert.match(world,/prime-account-no/,'New-account choice must open registration');
assert.match(world,/prime-registration-tab/,'Registration tab must be synchronized with authentication state');

for(const hook of ["beginOnlineQueue","startOnlineMatch","PrimeGameSnapshot","PrimeApplyRemoteInput","PrimeReadInput","PrimeApplyHostState","PrimeOnlineFinished"])assert.ok(world.includes(hook),"Missing Android online match integration: "+hook);
assert.ok(world.includes("bot.isRemotePlayer&&match.online?.role==='host'"),"Android host must simulate remote opponent input");
assert.ok(world.includes("if(match.online?.role==='guest'){drawArena();}else{update(dt);"),"Android guest must apply host-authoritative match state");
assert.ok(world.includes("match.online?.role==='guest'){match.online.attackPulse=true"),"Android guest attack input must be sent to host");
assert.ok(html.includes("mobile-stick")&&html.includes("mobile-attack"),"Android touch controls must remain available in online matches");
assert.ok(world.includes("profile.elo=Number(stats.stats?.rating??profile.elo??0)")&&world.includes("st.rating??0"),"Android starting ELO must be zero");
console.log("Android Prime cross-platform online + zero-ELO assertions passed");

// Regression: some Android WebViews report landscape CSS orientation while the
// actual phone viewport is portrait. The root layout must therefore be width-driven.
assert.ok(html.includes('id="prime-mobile-root-layout-fix"'),"Mobile root layout fallback must be present");
assert.ok(html.includes("body:not(.battle-active) .shell{display:block!important;width:100%!important"),"Mobile shell must be forced to full width outside battle");
assert.ok(html.includes("body:not(.battle-active) .main{display:block!important;width:100%!important"),"Mobile main content must not collapse in a centered grid");
console.log("Android Prime mobile viewport/root-layout regression assertions passed");
assert.ok(html.includes('id="prime-region"')&&html.includes("Москва")&&html.includes("Сингапур")&&html.includes("Франкфурт")&&html.includes("Сан-Паулу")&&html.includes("Цюрих"),"Android settings must list supported Supabase execution regions");
assert.ok(online.includes("EDGE_REGION_CODES")&&online.includes("x-region")&&online.includes("captureEdgeRegion"),"Android must apply region preferences to authenticated requests and detect the actual region");
assert.ok(world.includes("window.PrimeOnlineEdgeRegion")&&world.includes("основная база"),"Android settings must explain the actual execution region and Stockholm database location");
assert.ok(html.includes('id="prime-layout-toggle"')&&html.includes('id="prime-layout-reset"'),"Android settings must allow moving and resetting HUD positions");
assert.ok(html.includes("prime-mobile-auth-layout")&&html.includes("width:min(620px,100%)")&&html.includes("min-height:56px"),"Android sign-in/register dialog must be larger and touch-friendly");
assert.ok(html.includes('id="hud-ping"'),"Android battle HUD must show a dedicated ping chip");
assert.ok(html.includes('id="prime-lobby-mode"')&&world.includes("ВМЕСТЕ ПРОТИВ БОТОВ"),"Android lobby must offer co-op versus bots");
assert.ok(online.includes("new RTCPeerConnection")&&online.includes("prime-game"),"Android must attempt a direct WebRTC gameplay channel");
assert.ok(online.includes("stun:stun.cloudflare.com:3478")&&online.includes("maxRetransmits:0"),"Android P2P must use global STUN and low-latency unordered packets");
assert.ok(online.includes("PrimeOnlineRelayPing")&&world.includes("activatePrimeUiLayoutEditor"),"Android must expose separate relay RTT and movable battle UI");
console.log("Android Prime P2P/region/co-op/auth/layout assertions passed");

// Remove literal backslash-n separators from markup: WebView renders them as visible text.
assert.ok(!html.includes(String.fromCharCode(92)+"n"),"Mobile launcher HTML must not render escaped newline tokens");
console.log("Android Prime visible-markup cleanup assertions passed");
